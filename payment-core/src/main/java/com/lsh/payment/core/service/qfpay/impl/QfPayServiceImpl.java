package com.lsh.payment.core.service.qfpay.impl;

import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.baseVo.BaseResponse;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.api.model.payment.PaymentRequest;
import com.lsh.payment.api.model.qfpay.QfPayContent;
import com.lsh.payment.api.model.qfpay.QfResponse;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.Async.payTaskModel;
import com.lsh.payment.core.model.payEnum.PayStatus;
import com.lsh.payment.core.model.payEnum.PayWay;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.service.AsyncService.AsyncEvent;
import com.lsh.payment.core.service.payLog.PayLogService;
import com.lsh.payment.core.service.payment.IPayChannelService;
import com.lsh.payment.core.service.payment.impl.PayBaseService;
import com.lsh.payment.core.service.payment.impl.PayDealService;
import com.lsh.payment.core.strategy.config.QFPayConfig;
import com.lsh.payment.core.strategy.payStrategy.PayStrategyContext;
import com.lsh.payment.core.strategy.payVo.qfpay.QFpayResponse;
import com.lsh.payment.core.util.PayAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/11/30
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.service.qfpay.impl
 * desc:
 */
@Service
@Transactional(readOnly = true)
public class QfPayServiceImpl implements IPayChannelService {

   private static Logger logger = LoggerFactory.getLogger(QfPayServiceImpl.class);

    @Autowired
    private PayBaseService payBaseService;

    @Autowired
    private PayDealService payDealService;

    @Autowired
    private PayLogService payLogService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse prepay(PaymentRequest paymentRequest) {

        //字段校验
        PayAssert.notNull(paymentRequest.getTrade_module(), ExceptionStatus.E1002002.getCode(), ExceptionStatus.E1002002.getMessage());
        if (!paymentRequest.getPay_way().equals(PayWay.ANDROID.getName())) {
            throw new BusinessException(ExceptionStatus.E1002002.getCode(), "payWay 字段错误");
        }

        QfResponse<QfPayContent> response = new QfResponse<>();
        QfPayContent content = this.payHandle(paymentRequest);
        response.setContent(content);
        response.setRet(Integer.parseInt(ExceptionStatus.SUCCESS.getCode()));
        response.setMsg(ExceptionStatus.SUCCESS.getMessage());
        return response;
    }

    /**
     * @param paymentRequest  钱方请求参数
     * @return QfResponse     钱方返回参数
     */
    public QfPayContent payHandle(PaymentRequest paymentRequest) {
        QfPayContent respContent = new QfPayContent();

        //入库
        PayDeal payDeal = payBaseService.prePayment(paymentRequest);

        //填写返回参数
        respContent.setTitle(MessageFormat.format(QFPayConfig.QF_PAY_TITLE, payDeal.getTradeId()));
        respContent.setAuthedAmount(payDeal.getRequestAmount().toString());
        respContent.setTrade_id(payDeal.getTradeId());
        respContent.setPayPaymentNo(payDeal.getPayPaymentNo());
        respContent.setChannelName(payDeal.getPayChannel());
        respContent.setPayStatus(1);

        //已经成功直接返回
        if (PayStatus.PAY_SUCCESS.getValue() == payDeal.getPayStatus()) {
            respContent.setHaspayed("true");//已经支付成功
            return respContent;
        }

        //调用第三方进行支付
        String qfPayResult = this.callQfHandle(payDeal, paymentRequest);
        respContent.setHaspayed("false");
        respContent.setResult(JSON.parseObject((qfPayResult)));

        //记录操作日志
        payLogService.insertLog(payDeal.getPayId(), payDeal.getPayPaymentNo(), payDeal.getTradeId(), PayStatus.CREATE_PAYMENT.getValue(), BusiConstant.OPERATE_SUCCESS, JSON.toJSONString(paymentRequest), JSON.toJSONString(respContent));

        return respContent;
    }

    /**
     * 调用第三方进行支付
     * @param payDeal         支付平台记录
     * @param paymentRequest  下单请求参数
     * @return                请求返回参数
     */
    private String callQfHandle(PayDeal payDeal, PaymentRequest paymentRequest) {
        PayDeal payDealUpd = new PayDeal();
        payDealUpd.setPayId(payDeal.getPayId());

        //调用钱方支付
        Map<String, String> qfRequestMap = new HashMap<>(5);
        qfRequestMap.put("payPaymentNo", payDeal.getPayPaymentNo());
        qfRequestMap.put("payAmount", payDeal.getRequestAmount().toString());
        qfRequestMap.put("channelType", paymentRequest.getChannel_type());
        PayStrategyContext payStrategyContext = new PayStrategyContext();
        QFpayResponse qfPayResponse = (QFpayResponse) payStrategyContext.generatePayParams(BusiConstant.QFPAY, qfRequestMap);

        //根据第三方返回信息分别处理
        if (qfPayResponse != null && BusiConstant.PAY_REQUEST_SUCCESS.equals(qfPayResponse.getCode())) {
            logger.info("调钱方下单成功:" + payDeal.getPayId());
            //更新流水中的钱方订单号
            payDealUpd.setChannelTransaction(qfPayResponse.getSyssn());
            payDealUpd.setPayStatus(PayStatus.PAYING.getValue());
            if (payDealService.updatePayDeal(payDealUpd) > 0) {
                //插入task
//                payBaseService.addPayTask(payDeal);
                AsyncEvent.post(new payTaskModel(payDeal));
                //返回参数处理
                return qfPayResponse.getMsg();

            } else {
                logger.error("下单后,更新支付流水失败:" + payDeal.getPayPaymentNo());
                throw new BusinessException(ExceptionStatus.E2003001.getCode(), ExceptionStatus.E2003001.getMessage());
            }
        } else {
            logger.error("调钱方下单失败:" + payDeal.getPayId());
            throw new BusinessException(ExceptionStatus.E2001005.getCode(), ExceptionStatus.E2001005.getMessage());
        }
    }
}
