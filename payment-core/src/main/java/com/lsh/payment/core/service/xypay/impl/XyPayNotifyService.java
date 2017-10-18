package com.lsh.payment.core.service.xypay.impl;

import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.payEnum.OperateStatus;
import com.lsh.payment.core.model.payEnum.PayStatus;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.service.payment.impl.PayBaseService;
import com.lsh.payment.core.service.payment.impl.PayDealService;
import com.lsh.payment.core.service.qfpay.QFPayHandleService;
import com.lsh.payment.core.service.xypay.IXyPayNotifyService;
import com.lsh.payment.core.util.DateUtil;
import com.lsh.payment.core.util.PayAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 17/8/23.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service
@Transactional(readOnly = true)
public class XyPayNotifyService implements IXyPayNotifyService {

    private Logger logger = LoggerFactory.getLogger(QFPayHandleService.class);

    @Autowired
    private PayBaseService payBaseService;

    @Autowired
    private PayDealService payDealService;
    /**
     * 回调接口
     *
     * @param dataMap
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void dealNotify(Map<String, String> dataMap) {
        PayDeal payDeal;

        this.chenkParams(dataMap);

        PayDeal payDealUpd = new PayDeal();

        payDealUpd.setPayPaymentNo(dataMap.get("out_trade_no"));
        payDealUpd.setChannelTransaction(dataMap.get("transaction_id"));

        Map<String, Object> queryMap = new HashMap<>();

        queryMap.put("payPaymentNo", payDealUpd.getPayPaymentNo());
        List<PayDeal> payDeals = payDealService.queryPayDeals(queryMap);
        if (payDeals == null || payDeals.size() == 0) {
            logger.error("没有查到记录:", dataMap);
            throw new BusinessException(ExceptionStatus.E2001004.getCode(), ExceptionStatus.E2001004.getMessage());
        }
        payDeal = payDeals.get(0);
        payDealUpd.setPayId(payDeal.getPayId());

        //报文和支付流水信息进行校验
        //校验金额
        if (payDeal.getRequestAmount().multiply(new BigDecimal("100")).compareTo(new BigDecimal(dataMap.get("total_fee"))) != 0) {

            logger.error("交易金额不一致,支付流水:" + payDeal.getPayId() + "支付系统:" + payDeal.getRequestAmount() + "元,钱方:" + dataMap.get("total_fee") + "分");
            throw new BusinessException(ExceptionStatus.E2001002.getCode(), "金额不一致," + ExceptionStatus.E2001002.getMessage());
        }
        //校验支付方式
//        if (!chenkParams(qfNotifyInfo.getPay_type(), payDeal.getTradeType())) {
//            throw new BusinessException(ExceptionStatus.E2001002.getCode(), "支付方式不一致," + ExceptionStatus.E2001002.getMessage());
//        }
        payDealUpd.setReceiveAmount(payDeal.getRequestAmount());

        if (dataMap.get("status").equals("0") && dataMap.get("result_code").equals("0") && dataMap.get("pay_result").equals("0")) {//支付成功

            if (payDeal.getPayStatus() != PayStatus.PAY_SUCCESS.getValue()) {//非终态

                payDealUpd.setPayStatus(PayStatus.PAY_SUCCESS.getValue());
                payDealUpd.setOperateStatus(OperateStatus.PAYMENT_CALLBACK.getCode());
                payDealUpd.setPayTime(DateUtil.parsePayDate(dataMap.get("time_end")));
                payDealUpd.setDoneTime(new Date());

                payBaseService.updPayDeal(payDealUpd, payDeal);
            }
        }
    }

    /**
     *
     * @param dataMap
     */
    private void chenkParams(Map<String, String> dataMap) {

        PayAssert.notNull(dataMap, ExceptionStatus.E1002002.getCode(), ExceptionStatus.E1002002.getMessage());

        PayAssert.notNull(dataMap.get("out_trade_no"), ExceptionStatus.E1002002.getCode(), ExceptionStatus.E1002002.getMessage());

        PayAssert.notNull(dataMap.get("transaction_id"), ExceptionStatus.E1002002.getCode(), ExceptionStatus.E1002002.getMessage());

        PayAssert.notNull(dataMap.get("openid"), ExceptionStatus.E1002002.getCode(), ExceptionStatus.E1002002.getMessage());

        PayAssert.notNull(dataMap.get("result_code"), ExceptionStatus.E1002002.getCode(), ExceptionStatus.E1002002.getMessage());

        PayAssert.notNull(dataMap.get("status"), ExceptionStatus.E1002002.getCode(), ExceptionStatus.E1002002.getMessage());

        PayAssert.notNull(dataMap.get("mch_id"), ExceptionStatus.E1002002.getCode(), ExceptionStatus.E1002002.getMessage());

        PayAssert.notNull(dataMap.get("pay_result"), ExceptionStatus.E1002002.getCode(), ExceptionStatus.E1002002.getMessage());

    }


}
