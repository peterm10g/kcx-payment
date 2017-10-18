package com.lsh.payment.core.service.qfpay;

import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.payEnum.OperateStatus;
import com.lsh.payment.core.model.payEnum.PayStatus;
import com.lsh.payment.core.model.payEnum.TradeType;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.service.payment.impl.PayBaseService;
import com.lsh.payment.core.service.payment.impl.PayDealService;
import com.lsh.payment.core.strategy.config.QFPayConfig;
import com.lsh.payment.core.strategy.payVo.qfpay.QfNotifyInfo;
import com.lsh.payment.core.util.DateUtil;
import com.lsh.payment.core.util.PayAssert;
import com.lsh.payment.core.util.StringUtil;
import com.lsh.payment.core.util.pay.qfpay.QFSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/10/31
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.service.payment
 * desc:申请支付,处理流程
 */
@Service
@Transactional(readOnly = true)
public class QFPayHandleService {

    private Logger logger = LoggerFactory.getLogger(QFPayHandleService.class);

    @Autowired
    private PayBaseService payBaseService;

    @Autowired
    private PayDealService payDealService;


    /**
     * qf回调处理
     * @param sign 签名
     * @param body 回调数据
     * @return 支付结果记录
     */
    @Transactional(rollbackFor = Exception.class)
    public PayDeal notifyHandle(String sign, String body) {
        PayDeal payDeal;
        if (QFSignature.check(body, sign)) {//验签成功

            QfNotifyInfo qfNotifyInfo = JSON.parseObject(body, QfNotifyInfo.class);

            this.chenkParams(qfNotifyInfo);

            PayDeal payDealUpd = new PayDeal();

            payDealUpd.setPayPaymentNo(qfNotifyInfo.getOut_trade_no());
            payDealUpd.setChannelTransaction(qfNotifyInfo.getSyssn());

            Map<String, Object> queryMap = StringUtil.toMap(body);

            queryMap.put("payPaymentNo", payDealUpd.getPayPaymentNo());
//            queryMap.put("channelTransaction", payDeal.getChannelTransaction());

            List<PayDeal> payDeals = payDealService.queryPayDeals(queryMap);
            if (payDeals == null || payDeals.size() == 0) {
                logger.error("没有查到记录:", body);
                throw new BusinessException(ExceptionStatus.E2001004.getCode(), ExceptionStatus.E2001004.getMessage());
            }
            payDeal = payDeals.get(0);
            payDealUpd.setPayId(payDeal.getPayId());

            //报文和支付流水信息进行校验
            //校验金额
            if (payDeal.getRequestAmount().multiply(new BigDecimal("100")).compareTo(new BigDecimal(qfNotifyInfo.getTxamt())) != 0) {

                logger.error("交易金额不一致,支付流水:" + payDeal.getPayId() + "支付系统:" + payDeal.getRequestAmount() + "元,钱方:" + qfNotifyInfo.getTxamt() + "分");
                throw new BusinessException(ExceptionStatus.E2001002.getCode(), "金额不一致," + ExceptionStatus.E2001002.getMessage());
            }
            //校验支付方式
            if (!chenkParams(qfNotifyInfo.getPay_type(), payDeal.getTradeType())) {
                throw new BusinessException(ExceptionStatus.E2001002.getCode(), "支付方式不一致," + ExceptionStatus.E2001002.getMessage());
            }
            payDealUpd.setReceiveAmount(payDeal.getRequestAmount());

//            && qfNotifyInfo.getStatus().equals(QFPayConfig.CALL_BACK_SUCCESS)
            if (qfNotifyInfo.getRespcd().equals(QFPayConfig.RESPCD_SUCCESS)) {//支付成功

                if (payDeal.getPayStatus() != PayStatus.PAY_SUCCESS.getValue()) {//非终态

                    payDealUpd.setPayStatus(PayStatus.PAY_SUCCESS.getValue());
                    payDealUpd.setOperateStatus(OperateStatus.PAYMENT_CALLBACK.getCode());
                    payDealUpd.setPayTime(DateUtil.parsePayDate_1(String.valueOf(qfNotifyInfo.getSysdtm())));
                    payDealUpd.setDoneTime(new Date());

                    payBaseService.updPayDeal(payDealUpd, payDeal);

                }
            }
        } else {

            logger.error("验签失败,报文:" + body + "签名:" + sign);
            throw new BusinessException(ExceptionStatus.E2001001.getCode(), ExceptionStatus.E2001001.getMessage());
        }
        return payDeal;
    }

    /**
     * 参数校验
     * @param pay_type 支付类型
     * @param tradeType  交易类型
     * @return  boolean
     */
    public static boolean chenkParams(String pay_type, Integer tradeType) {
        if (tradeType.equals(TradeType.QFALISM.getCode())) {
            if (QFPayConfig.PAY_TYPE_ALI.equals(pay_type)) {
                return true;
            }
        } else if (tradeType.equals(TradeType.QFWXSM.getCode())) {
            if (QFPayConfig.PAY_TYPE_WX.equals(pay_type)) {
                return true;
            }
        }
        return false;
    }


    private void chenkParams(QfNotifyInfo qfNotifyInfo) {

        PayAssert.notNull(qfNotifyInfo, ExceptionStatus.E1002002.getCode(), ExceptionStatus.E1002002.getMessage());

        PayAssert.notNull(qfNotifyInfo.getOut_trade_no(), ExceptionStatus.E1002002.getCode(), ExceptionStatus.E1002002.getMessage());

        PayAssert.notNull(qfNotifyInfo.getSyssn(), ExceptionStatus.E1002002.getCode(), ExceptionStatus.E1002002.getMessage());

        PayAssert.notNull(qfNotifyInfo.getTxamt(), ExceptionStatus.E1002002.getCode(), ExceptionStatus.E1002002.getMessage());

        PayAssert.notNull(qfNotifyInfo.getCancel(), ExceptionStatus.E1002002.getCode(), ExceptionStatus.E1002002.getMessage());

        PayAssert.notNull(qfNotifyInfo.getNotify_type(), ExceptionStatus.E1002002.getCode(), ExceptionStatus.E1002002.getMessage());

        PayAssert.notNull(qfNotifyInfo.getPay_type(), ExceptionStatus.E1002002.getCode(), ExceptionStatus.E1002002.getMessage());

        PayAssert.notNull(qfNotifyInfo.getRespcd(), ExceptionStatus.E1002002.getCode(), ExceptionStatus.E1002002.getMessage());

//        PayAssert.notNull(qfNotifyInfo.getStatus(), ExceptionStatus.E1002002.getCode(), ExceptionStatus.E1002002.getMessage());

        PayAssert.notNull(qfNotifyInfo.getTxcurrcd(), ExceptionStatus.E1002002.getCode(), ExceptionStatus.E1002002.getMessage());

        if (!QFPayConfig.CANCLE_NOMAL.equals(qfNotifyInfo.getCancel())) {
            logger.error("报文异常,报文:" + JSON.toJSONString(qfNotifyInfo));
            throw new BusinessException(ExceptionStatus.E2001001.getCode(), ExceptionStatus.E2001001.getMessage());
        }

        if (!QFPayConfig.ORDER_TYPE_PAYMENT.equals(qfNotifyInfo.getNotify_type())) {
            logger.error("报文异常,报文:" + JSON.toJSONString(qfNotifyInfo));
            throw new BusinessException(ExceptionStatus.E2001001.getCode(), ExceptionStatus.E2001001.getMessage());
        }

        if (!QFPayConfig.RMB.equals(qfNotifyInfo.getTxcurrcd())) {
            logger.error("报文异常,报文:" + JSON.toJSONString(qfNotifyInfo));
            throw new BusinessException(ExceptionStatus.E2001001.getCode(), ExceptionStatus.E2001001.getMessage());
        }

    }

}
