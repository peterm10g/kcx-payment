package com.lsh.payment.core.service.wxPay.impl;

import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.baseVo.BaseResponse;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.api.model.payment.PaymentRequest;
import com.lsh.payment.api.model.wxpay.*;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.Async.payTaskModel;
import com.lsh.payment.core.model.payEnum.*;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.service.AsyncService.AsyncEvent;
import com.lsh.payment.core.service.payLog.PayLogService;
import com.lsh.payment.core.service.payment.IPayChannelService;
import com.lsh.payment.core.service.payment.impl.PayBaseService;
import com.lsh.payment.core.strategy.config.WxPayConfig;
import com.lsh.payment.core.strategy.payStrategy.PayStrategyContext;
import com.lsh.payment.core.strategy.payVo.wxpay.WxPayResponse;
import com.lsh.payment.core.util.MD5Util;
import com.lsh.payment.core.util.PayAssert;
import com.lsh.payment.core.util.pay.weChatpay.WxSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/25.
 * 商务有限公司
 * Package
 * desc:
 */
@Service
@Transactional(readOnly = true)
public class WxPayServiceImpl implements IPayChannelService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PayBaseService payBaseService;

    @Autowired
    private PayLogService payLogService;


    /**
     * app H5
     *
     * @param paymentRequest 微信预下单对象
     * @return BaseResponse
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse prepay(PaymentRequest paymentRequest) {

        paymentRequest.setTrade_module(TradeModule.ORDER.getName());

        if (paymentRequest.getPay_way().equals(PayWay.ANDROID.getName()) || paymentRequest.getPay_way().equals(PayWay.IOS.getName())) {

            return this.payApp(paymentRequest);

        } else if (paymentRequest.getPay_way().equals(PayWay.H5.getName())) {

            PayAssert.notNull(paymentRequest.getReturn_url(), ExceptionStatus.E1002001.getCode(), "return_url为空");
            PayAssert.notNull(paymentRequest.getOpenid(), ExceptionStatus.E1002001.getCode(), "openid 为空");

            paymentRequest.setChannel_type(String.valueOf(TradeType.WXH5.getCode()));

            return this.payH5(paymentRequest);

        } else {

            throw new BusinessException(ExceptionStatus.E2001002.getCode(), "pay_way 参数异常");
        }

    }



    /**
     * 微信app预下单
     *
     * @param paymentRequest 微信预下单对象
     * @return 微信app预下单结果
     */
    private BaseResponse payApp(PaymentRequest paymentRequest) {

        WxResponse<WxAppContent> wxResponse = new WxResponse<>();

        PayDeal payDeal = payBaseService.prePayment(paymentRequest);

        Map<String, String> payParams = new HashMap<>(4);
        payParams.put("payWay", paymentRequest.getPay_way());//用于选择channel
        payParams.put("payPaymentNo", payDeal.getPayPaymentNo());
        payParams.put("payId", payDeal.getPayId());
        payParams.put("payAmount", payDeal.getRequestAmount().toString());

        payParams.put("system", paymentRequest.getSystem().toString());

        PayStrategyContext payStrategyContext = new PayStrategyContext();
        WxPayResponse wxPayResponse = (WxPayResponse) payStrategyContext.generatePayParams(BusiConstant.WECHATPAY, payParams);

        if (wxPayResponse == null || wxPayResponse.getCode() == null || wxPayResponse.getCode().equals(BusiConstant.PAY_REQUEST_FAIL)) {

            logger.info("微信申请支付授权失败 : " + ExceptionStatus.E2001005.getMessage());
            throw new BusinessException(ExceptionStatus.E2001005.getCode(), ExceptionStatus.E2001005.getMessage());
        }

        if (wxPayResponse.getReturn_code().equals("SUCCESS") && wxPayResponse.getResult_code().equals("SUCCESS")) {

            AsyncEvent.post(new payTaskModel(payDeal));

            wxResponse.setContent(this.getWxAppContent(payDeal, wxPayResponse));
            wxResponse.setRet(Integer.parseInt(ExceptionStatus.SUCCESS.getCode()));
            wxResponse.setMsg(ExceptionStatus.SUCCESS.getMessage());
        } else {

            throw new BusinessException(ExceptionStatus.E2001005.getCode(), ExceptionStatus.E2001005.getMessage());
        }

        //记录操作日志
        payLogService.insertLog(payDeal.getPayId(), payDeal.getPayPaymentNo(), payDeal.getTradeId(), PayStatus.CREATE_PAYMENT.getValue(), BusiConstant.OPERATE_SUCCESS, JSON.toJSONString(paymentRequest), JSON.toJSONString(wxResponse));

        return wxResponse;
    }

    /**
     * 微信app预下单返回值
     *
     * @param payDeal       支付记录
     * @param wxPayResponse 请求微信预下单接口返回结果
     * @return 返回支付平台预下单结果
     */
    private WxAppContent getWxAppContent(PayDeal payDeal, WxPayResponse wxPayResponse) {

        String nsoncestr = MD5Util.generateNonceStr();

        WxAppResult result = new WxAppResult();
        result.setAppid(wxPayResponse.getAppid());
        result.setNoncestr(nsoncestr);
        result.setWxAppPackage(WxPayConfig.WX_APP_PACKAGE);
        result.setPartnerid(WxPayConfig.MCHID_APP);
        result.setPrepayid(wxPayResponse.getPrepay_id());
        result.setTimestamp(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
        result.setSign(this.getWxAppSign(wxPayResponse, nsoncestr));

        WxAppContent content = new WxAppContent();
        content.setPayPaymentNo(payDeal.getPayPaymentNo());
        content.setChannelName(PayChannel.WXPAY.getName());
        content.setAuthedAmount(payDeal.getRequestAmount().toString());
        content.setResult(result);

        return content;
    }

    /**
     * 生成微信签名
     *
     * @param wxPayResponse 微信预下单返回对象
     * @return 微信签名
     */
    private String getWxAppSign(WxPayResponse wxPayResponse, String nsoncestr) {
        Map<String, String> result = new HashMap<>();
        result.put("appid", wxPayResponse.getAppid());
        result.put("noncestr", nsoncestr);
        result.put("package", WxPayConfig.WX_APP_PACKAGE);
        result.put("partnerid", WxPayConfig.MCHID_APP);
        result.put("prepayid", wxPayResponse.getPrepay_id());
        result.put("timestamp", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));

        return WxSignature.getNewSign(result, WxPayConfig.KEY_APP);
    }

    /**
     * 微信H5预下单
     *
     * @param paymentRequest 微信预下单对象
     * @return 微信app预下单结果
     */
    private BaseResponse payH5(PaymentRequest paymentRequest) {

        WxResponse<WxH5Content> wxResponse = new WxResponse<>();

        PayDeal payDeal = payBaseService.prePayment(paymentRequest);

        Map<String, String> payParams = new HashMap<>(5);

        payParams.put("openid", paymentRequest.getOpenid());
        payParams.put("payWay", paymentRequest.getPay_way());//用于选择channel
        payParams.put("payPaymentNo", payDeal.getPayPaymentNo());
        payParams.put("payId", payDeal.getPayId());
        payParams.put("payAmount", payDeal.getRequestAmount().toString());

        payParams.put("system", paymentRequest.getSystem().toString());

        PayStrategyContext payStrategyContext = new PayStrategyContext();
        WxPayResponse wxPayResponse = (WxPayResponse) payStrategyContext.generatePayParams(BusiConstant.WECHATPAY, payParams);

        if (wxPayResponse == null || wxPayResponse.getCode() == null || wxPayResponse.getCode().equals(BusiConstant.PAY_REQUEST_FAIL)) {

            logger.info("微信申请支付授权失败 : " + ExceptionStatus.E2001005.getMessage());
            throw new BusinessException(ExceptionStatus.E2001005.getCode(), ExceptionStatus.E2001005.getMessage());
        }

        if (wxPayResponse.getReturn_code().equals("SUCCESS") && wxPayResponse.getResult_code().equals("SUCCESS")) {

            AsyncEvent.post(new payTaskModel(payDeal));

            wxResponse.setContent(this.getWxH5Content(wxPayResponse, payDeal, paymentRequest));
            wxResponse.setRet(Integer.parseInt(ExceptionStatus.SUCCESS.getCode()));
            wxResponse.setMsg(ExceptionStatus.SUCCESS.getMessage());

        } else {

            throw new BusinessException(ExceptionStatus.E2001005.getCode(), ExceptionStatus.E2001005.getMessage());
        }

        return wxResponse;
    }

    /**
     * 微信H5预下单返回值
     *
     * @param payDeal       支付记录
     * @param wxPayResponse 请求微信预下单接口返回结果
     * @return 返回支付平台预下单结果
     */
    private WxH5Content getWxH5Content(WxPayResponse wxPayResponse, PayDeal payDeal, PaymentRequest paymentRequest) {
        WxH5Result result = new WxH5Result();

        String nonceStr = MD5Util.generateNonceStr();

        result.setAppId(wxPayResponse.getAppid());
        result.setNonceStr(nonceStr);
        result.setWxH5Package(MessageFormat.format(WxPayConfig.WX_H5_PACKAGE, wxPayResponse.getPrepay_id()));
        result.setSignType(WxPayConfig.WX_H5_SIGNTYPE);
        String time = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        result.setTimeStamp(time);
        result.setPaySign(this.getWxH5Sgin(wxPayResponse, nonceStr,paymentRequest,time));

        WxH5Content content = new WxH5Content();

        content.setTitle(MessageFormat.format(WxPayConfig.PAY_TITLE_NAME, payDeal.getTradeId()));
        content.setAuthedAmount(payDeal.getRequestAmount().toString());
        content.setReturnUrl(paymentRequest.getReturn_url());
        content.setOpenid(paymentRequest.getOpenid());
        content.setPayPaymentNo(payDeal.getPayPaymentNo());
        content.setChannelName(PayChannel.WXPAY.getName());
        content.setPayStatus(PayStatus.CREATE_PAYMENT.getValue());

        content.setResult(result);

        return content;
    }

    /**
     * 生成微信签名
     *
     * @param wxPayResponse wxPayResponse
     * @param nonceStr      nonceStr
     * @return String
     */
    private String getWxH5Sgin(WxPayResponse wxPayResponse, String nonceStr,PaymentRequest paymentRequest,String time) {

        Map<String, String> result = new HashMap<>(8);

        result.put("appId", wxPayResponse.getAppid());
        result.put("nonceStr", nonceStr);
        result.put("package", MessageFormat.format(WxPayConfig.WX_H5_PACKAGE, wxPayResponse.getPrepay_id()));
        result.put("signType", WxPayConfig.WX_H5_SIGNTYPE);
        result.put("timeStamp", time);

        if(paymentRequest.getSystem().compareTo(CallSystem.YG.getCode()) == 0){
            return WxSignature.getNewSign(result, WxPayConfig.KEY);
        }else{
            return WxSignature.getNewSign(result, WxPayConfig.GROUPON_KEY);
        }

    }



}
