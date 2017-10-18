package com.lsh.payment.core.service.wxPay.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.service.wxPay.IWxRefundService;
import com.lsh.payment.core.strategy.config.WxPayConfig;
import com.lsh.payment.core.strategy.payVo.wxpay.WxPayBaseResult;
import com.lsh.payment.core.strategy.payVo.wxpay.WxPayRefundQueryResult;
import com.lsh.payment.core.strategy.payVo.wxpay.WxPayRefundResult;
import com.lsh.payment.core.util.*;
import com.lsh.payment.core.util.pay.weChatpay.WxSignature;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 17/6/29.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service
public class WxRefundServiceImpl implements IWxRefundService {

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(WxRefundServiceImpl.class);

    @Override
    public WxPayRefundResult refund(Map<String, String> dataMap) {
        String wxpaykey = WxPayConfig.GROUPON_KEY;
        Map<String, String> wxRefundRequestBody = new HashMap<>(10);
        wxRefundRequestBody.put("appid", WxPayConfig.GROUPON_APPID);// 应用ID
        wxRefundRequestBody.put("mch_id", WxPayConfig.GROUPON_MCHID);// 商户号

        wxRefundRequestBody.put("nonce_str", MD5Util.generateNonceStr());//随机串
        wxRefundRequestBody.put("transaction_id", dataMap.get("transaction_id"));//微信支付流水号
        wxRefundRequestBody.put("out_refund_no", dataMap.get("out_refund_no"));//支付平台退款流水号
        wxRefundRequestBody.put("total_fee", dataMap.get("total_fee"));
        wxRefundRequestBody.put("refund_fee", dataMap.get("refund_fee"));

        logger.info("退款-申请授权数据 : " + JSON.toJSONString(wxRefundRequestBody));
        // 签名
        wxRefundRequestBody.put("sign", WxSignature.getNewSign(wxRefundRequestBody, wxpaykey));

        String wxRefundRequestXml = XmlUtil.toXml(wxRefundRequestBody);
        logger.info("退款-请求微信下单,参数:" + wxRefundRequestXml);

        String wxRefundResponseXml = HttpsClientUtil.doPostXml(WxPayConfig.REFUND_API, wxRefundRequestXml);
        if (StringUtils.isBlank(wxRefundResponseXml)) {
            throw new BusinessException(ExceptionStatus.E2001005.getCode(), "退款下单失败");
        }

        WxPayRefundResult result = WxPayBaseResult.fromXML(wxRefundResponseXml, WxPayRefundResult.class);

        logger.info(dataMap.get("out_refund_no") + "-WxPayRefundResult is " + result);

        result.checkResult();

        return result;
    }

    @Override
    public WxPayRefundQueryResult refundQuery(Map<String, String> dataMap) {
        String wxpaykey = WxPayConfig.GROUPON_KEY;
        Map<String, String> wxRefundQueryRequestBody = new HashMap<>(10);
        wxRefundQueryRequestBody.put("appid", WxPayConfig.GROUPON_APPID);// 应用ID
        wxRefundQueryRequestBody.put("mch_id", WxPayConfig.GROUPON_MCHID);// 商户号
        wxRefundQueryRequestBody.put("nonce_str", MD5Util.generateNonceStr());//随机串
        wxRefundQueryRequestBody.put("out_refund_no", dataMap.get("paymentRefundNo"));//支付平台退款流水号
        if(StringUtils.isNotEmpty(dataMap.get("refundId"))){
            wxRefundQueryRequestBody.put("refund_id", dataMap.get("refundId"));//微信支付流水号
        }

        logger.info("申请授权数据 : " + JSON.toJSONString(wxRefundQueryRequestBody));
        // 签名
        wxRefundQueryRequestBody.put("sign", WxSignature.getNewSign(wxRefundQueryRequestBody, wxpaykey));

        String wxRefundQueryRequestXml = XmlUtil.toXml(wxRefundQueryRequestBody);
        logger.info("请求微信退款查询,参数:" + wxRefundQueryRequestXml);

        String wxRefundQueryResponseXml = HttpClientUtils.doPostXml(WxPayConfig.REFUND_QUERY_API, wxRefundQueryRequestXml);
        if (StringUtils.isBlank(wxRefundQueryResponseXml)) {
            throw new BusinessException(ExceptionStatus.E2001005.getCode(), ExceptionStatus.E2001005.getMessage());
        }

        WxPayRefundQueryResult result = WxPayBaseResult.fromXML(wxRefundQueryResponseXml, WxPayRefundQueryResult.class);
        result.composeRefundRecords();
        result.checkResult();

        return result;
    }
}
