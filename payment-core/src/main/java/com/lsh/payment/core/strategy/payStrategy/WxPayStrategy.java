package com.lsh.payment.core.strategy.payStrategy;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.payEnum.PayWay;
import com.lsh.payment.core.strategy.config.WxPayConfig;
import com.lsh.payment.core.strategy.payVo.wxpay.WxPayResponse;
import com.lsh.payment.core.util.*;
import com.lsh.payment.core.util.pay.weChatpay.WeChatCore;
import com.lsh.payment.core.util.pay.weChatpay.WxSignature;
import net.sf.json.JSONObject;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信app支付
 * Project Name: lsh-payment
 * Created by peter on 16/10/30.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class WxPayStrategy implements PayStrategy<WxPayResponse> {

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(WxPayStrategy.class);

    @Override
    public WxPayResponse generatePayParams(int payType, final Map<String, String> paramMap) {

        WxPayResponse wxPayRresponse;

        String wxpaykey;
        Map<String, String> wxpayRequestBody = new HashMap<>(18);
        // 检测必填参数

        //APP和H5参数不一致
        if (paramMap.get("payWay").equals(PayWay.IOS.getName()) || paramMap.get("payWay").equals(PayWay.ANDROID.getName())) {
            wxpayRequestBody.put("appid", WxPayConfig.APPID_APP);// 应用ID
            wxpayRequestBody.put("mch_id", WxPayConfig.MCHID_APP);// 商户号
            wxpayRequestBody.put("notify_url", WxPayConfig.NOTIFY_URL_APP);
            wxpayRequestBody.put("trade_type", WxPayConfig.TRADE_TYPE_APP);
            wxpaykey = WxPayConfig.KEY_APP;
        } else if (paramMap.get("payWay").equals(PayWay.H5.getName())) {
            //openid
            String openid = paramMap.get("openid");
            if (StringUtils.isBlank(openid)) {
                logger.error("openid is null:" + paramMap.get("payId"));
                return null;
            }
            if(paramMap.get("system").equals("1")){
                wxpaykey = WxPayConfig.KEY;
                wxpayRequestBody.put("appid", WxPayConfig.APPID);// 应用ID
                wxpayRequestBody.put("mch_id", WxPayConfig.MCHID);// 商户号
            }else if(paramMap.get("system").equals("2")){
                logger.info("groupon h5 prepay order for pay ");
                wxpaykey = WxPayConfig.GROUPON_KEY;
                wxpayRequestBody.put("appid", WxPayConfig.GROUPON_APPID);// 应用ID
                wxpayRequestBody.put("mch_id", WxPayConfig.GROUPON_MCHID);// 商户号
            }else {
                logger.info("system para is : " + paramMap.get("system"));
                return null;
            }

            wxpayRequestBody.put("notify_url", WxPayConfig.NOTIFY_URL);
            wxpayRequestBody.put("trade_type", WxPayConfig.TRADE_TYPE);
            wxpayRequestBody.put("openid", openid);

        } else {
            logger.info("pay_way para is : " + paramMap.get("payWay"));
            return null;
        }

        wxpayRequestBody.put("nonce_str", MD5Util.generateNonceStr());// 随机字符串

       String system =  paramMap.get("system");
        if(StringUtils.isNotEmpty(system) && system.equals("2")){
            wxpayRequestBody.put("body", WxPayConfig.GROUPON_GOODS_NAME);
        }else{
            wxpayRequestBody.put("body", WxPayConfig.GOODS_NAME);
        }

        wxpayRequestBody.put("out_trade_no", paramMap.get("payPaymentNo"));
        wxpayRequestBody.put("total_fee", BigDecimalUtils.yuanToFen(paramMap.get("payAmount")).toString());
        wxpayRequestBody.put("spbill_create_ip", WxPayConfig.IP);// 终端ip

        logger.info("申请授权数据 : " + JSON.toJSONString(wxpayRequestBody));
        // 签名
        wxpayRequestBody.put("sign", WxSignature.getNewSign(wxpayRequestBody, wxpaykey));

        logger.info("带签名,申请授权数据 : " + JSON.toJSONString(wxpayRequestBody));

        String wxpayRequestXml = XmlUtil.toXml(wxpayRequestBody);
        logger.info("请求微信下单,参数:" + wxpayRequestXml);
        String wxpayResponseXml = HttpClientUtils.doPostXml(WxPayConfig.PAY_API, wxpayRequestXml);
        if (StringUtils.isBlank(wxpayResponseXml)) {
            throw new BusinessException(ExceptionStatus.E2001005.getCode(), ExceptionStatus.E2001005.getMessage());
        }
        JSONObject wxpayResponseJson = XmlUtil.parseXmlJson(wxpayResponseXml);
        wxPayRresponse = (WxPayResponse) JSONObject.toBean(wxpayResponseJson, WxPayResponse.class);
        wxPayRresponse = (WxPayResponse) WeChatCore.mkResponse(wxpayResponseJson, wxPayRresponse, wxpaykey);
        logger.info("微信下单返回:" + JSON.toJSONString(wxPayRresponse));

        return wxPayRresponse;

    }


}
