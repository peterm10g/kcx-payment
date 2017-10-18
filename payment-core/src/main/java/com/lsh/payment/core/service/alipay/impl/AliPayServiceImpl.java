package com.lsh.payment.core.service.alipay.impl;

import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.alipay.AliAppContent;
import com.lsh.payment.api.model.alipay.AliH5Content;
import com.lsh.payment.api.model.alipay.AliResponse;
import com.lsh.payment.api.model.baseVo.BaseResponse;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.api.model.payment.PaymentRequest;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.Async.payTaskModel;
import com.lsh.payment.core.model.payEnum.PayStatus;
import com.lsh.payment.core.model.payEnum.PayWay;
import com.lsh.payment.core.model.payEnum.TradeModule;
import com.lsh.payment.core.model.payEnum.TradeType;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.service.AsyncService.AsyncEvent;
import com.lsh.payment.core.service.payLog.PayLogService;
import com.lsh.payment.core.service.payment.IPayChannelService;
import com.lsh.payment.core.service.payment.impl.PayBaseService;
import com.lsh.payment.core.strategy.config.AliPayConfig;
import com.lsh.payment.core.strategy.payStrategy.PayStrategyContext;
import com.lsh.payment.core.strategy.payVo.alipay.AliPrePayResponse;
import com.lsh.payment.core.util.PayAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/25.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service
@Transactional(readOnly = true)
public class AliPayServiceImpl implements IPayChannelService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PayBaseService payBaseService;

    @Autowired
    private PayLogService payLogService;

    /**
     * app H5 统一下单
     *
     * @param paymentRequest 下单参数对象
     * @return               下单结果值
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse prepay(PaymentRequest paymentRequest) {

        paymentRequest.setTrade_module(TradeModule.ORDER.getName());

        String payWay = paymentRequest.getPay_way();

        if (payWay.equals(PayWay.ANDROID.getName()) || payWay.equals(PayWay.IOS.getName())) {
            return this.appPay(paymentRequest);
        } else if (payWay.equals(PayWay.H5.getName())) {
            PayAssert.notNull(paymentRequest.getReturn_url(), ExceptionStatus.E1002001.getCode(), "returnUrl为空");
            paymentRequest.setChannel_type(String.valueOf(TradeType.ALIH5.getCode()));

            return this.h5Pay(paymentRequest);
        } else {
            throw new BusinessException(ExceptionStatus.E2001002.getCode(),"pay_way 参数异常");
        }

    }

    /**
     * app 申请支付授权
     * @param paymentRequest  申请授权参数对象
     * @return
     */
    private BaseResponse appPay(PaymentRequest paymentRequest) {

        AliResponse<AliAppContent> aliResponse = new AliResponse<>();

        PayDeal payDeal = payBaseService.prePayment(paymentRequest);

        AliAppContent content = new AliAppContent();
        content.setChannelName(AliPayConfig.CHANNEL_NAME);
        content.setPayPaymentNo(payDeal.getPayPaymentNo());
        content.setAuthedAmount(payDeal.getRequestAmount().toString());
//        content.setResult(true);

        Map<String, String> payParams = new HashMap<>(6);

        payParams.put("payPaymentNo", payDeal.getPayPaymentNo());
        payParams.put("payAmount", payDeal.getRequestAmount().toString());
        payParams.put("payWay", paymentRequest.getPay_way());//用于选择channel

        PayStrategyContext payStrategyContext = new PayStrategyContext();
        AliPrePayResponse payResult = (AliPrePayResponse)payStrategyContext.generatePayParams(BusiConstant.ALIPAY, payParams);

        if (payResult == null || payResult.getCode() == null || payResult.getCode().equals(BusiConstant.PAY_REQUEST_FAIL)) {
            throw new BusinessException(ExceptionStatus.E3001001.getCode(), ExceptionStatus.E3001001.getMessage());
        }

//        content.setOrderInfo(payResult.getResult());
        content.setResult(payResult.getResult());
        aliResponse.setContent(content);
        AsyncEvent.post(new payTaskModel(payDeal));

        aliResponse.setRet(Integer.parseInt(ExceptionStatus.SUCCESS.getCode()));
        aliResponse.setMsg(ExceptionStatus.SUCCESS.getMessage());
        aliResponse.setTimestamp(new Date());

        //记录操作日志
        payLogService.insertLog(payDeal.getPayId(), payDeal.getPayPaymentNo(), payDeal.getTradeId(), PayStatus.CREATE_PAYMENT.getValue(), BusiConstant.OPERATE_SUCCESS, JSON.toJSONString(paymentRequest), JSON.toJSONString(aliResponse));

        return aliResponse;
    }

    /**
     * H5 申请授权
     * @param paymentRequest 申请授权参数对象
     * @return               返回结果对象
     */
    private BaseResponse h5Pay(PaymentRequest paymentRequest) {

        AliResponse<AliH5Content> aliResponse = new AliResponse<>();

        PayDeal payDeal = payBaseService.prePayment(paymentRequest);

        Map<String, String> payParams = new HashMap<>(6);

        payParams.put("payPaymentNo", payDeal.getPayPaymentNo());
        payParams.put("payAmount", payDeal.getRequestAmount().toString());
        payParams.put("returnUrl", paymentRequest.getReturn_url());
        payParams.put("payWay", paymentRequest.getPay_way());//用于选择channel

        PayStrategyContext payStrategyContext = new PayStrategyContext();
        AliPrePayResponse payResult = (AliPrePayResponse)payStrategyContext.generatePayParams(BusiConstant.ALIPAY, payParams);

        if (payResult == null || payResult.getCode() == null || payResult.getCode().equals(BusiConstant.PAY_REQUEST_FAIL)) {

            throw new BusinessException(ExceptionStatus.E3001001.getCode(), ExceptionStatus.E3001001.getMessage());
        }

        //授权,添加task
//        if (!payDeal.getPayStatus().equals(PayStatus.PAYING.getValue())) {
//
//            payBaseService.authorizePayAndAddPayTask(payDeal);
//        }
//        payBaseService.addPayTask(payDeal);
        AsyncEvent.post(new payTaskModel(payDeal));

        AliH5Content content = new AliH5Content();
        content.setTitle(MessageFormat.format(AliPayConfig.ALIPAY_TITLE,payDeal.getPayId()));
        content.setAuthedAmount(payDeal.getRequestAmount().toString());
        content.setReturnUrl(paymentRequest.getReturn_url());
        content.setPayPaymentNo(payDeal.getPayPaymentNo());
        content.setChannelName(AliPayConfig.CHANNEL_NAME);
        content.setPayStatus(payDeal.getPayStatus());
        content.setResult(payResult.getResult());

        aliResponse.setContent(content);
        aliResponse.setRet(Integer.parseInt(ExceptionStatus.SUCCESS.getCode()));
        aliResponse.setMsg(ExceptionStatus.SUCCESS.getMessage());
        aliResponse.setTimestamp(new Date());

        //记录操作日志
        payLogService.insertLog(payDeal.getPayId(), payDeal.getPayPaymentNo(), payDeal.getTradeId(), PayStatus.CREATE_PAYMENT.getValue(), BusiConstant.OPERATE_SUCCESS, JSON.toJSONString(paymentRequest), JSON.toJSONString(aliResponse));
        return aliResponse;
    }

}
