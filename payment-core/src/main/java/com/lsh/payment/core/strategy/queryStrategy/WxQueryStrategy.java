package com.lsh.payment.core.strategy.queryStrategy;

import com.alibaba.fastjson.JSON;
import com.lsh.payment.core.util.MD5Util;
import com.lsh.payment.core.util.XmlUtil;
import com.lsh.payment.core.util.pay.weChatpay.WeChatCore;
import com.lsh.payment.core.util.pay.weChatpay.WxSignature;
import com.lsh.payment.core.strategy.config.WxPayConfig;
import com.lsh.payment.core.strategy.payVo.wxpay.WxQueryResponse;
import com.lsh.payment.core.model.payEnum.PayWay;
import com.lsh.payment.core.util.HttpClientUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/6.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class WxQueryStrategy implements QueryStrategy<WxQueryResponse> {

    private static Logger logger = LoggerFactory.getLogger(WxQueryStrategy.class);

    /**
     * 查询第三方支付状态
     *
     * @param paramMap 查询参数
     * @return WxQueryBasePayResponse
     */
    @Override
    public WxQueryResponse queryPayStatusByParams(Map<String, Object> paramMap) {


        WxQueryResponse wxQueryresponse;
        Map<String, String> wxQueryBody = new HashMap<>(8);
        String wxpayKey;
        if (PayWay.ANDROID.getName().equals(paramMap.get("pay_way")) || PayWay.IOS.getName().equals(paramMap.get("pay_way"))) {
            wxQueryBody.put("appid", WxPayConfig.APPID_APP);// 应用ID
            wxQueryBody.put("mch_id", WxPayConfig.MCHID_APP);// 商户号
            wxpayKey = WxPayConfig.KEY_APP;
        } else if (PayWay.H5.getName().equals(paramMap.get("pay_way"))) {
            Object system = paramMap.get("system");

            if(system != null && system.toString().equals("2")){
                wxQueryBody.put("appid", WxPayConfig.GROUPON_APPID);// 应用ID
                wxQueryBody.put("mch_id", WxPayConfig.GROUPON_MCHID);// 商户号
                wxpayKey = WxPayConfig.GROUPON_KEY;
            }else{
                wxQueryBody.put("appid", WxPayConfig.APPID);// 应用ID
                wxQueryBody.put("mch_id", WxPayConfig.MCHID);// 商户号
                wxpayKey = WxPayConfig.KEY;
            }

        } else {
            return null;
        }
//        sBody.put("transaction_id", paramMap.get("channelId"));
        wxQueryBody.put("out_trade_no", paramMap.get("out_trade_no").toString());
        wxQueryBody.put("nonce_str", MD5Util.generateNonceStr());// 随机字符串
        wxQueryBody.put("sign", WxSignature.getNewSign(wxQueryBody, wxpayKey));
        String wxpayRequestXml = XmlUtil.toXml(wxQueryBody);
        logger.info("查询请求微信,参数:" + wxpayRequestXml);
        String wxpayResponseXml = HttpClientUtils.doPostXml(WxPayConfig.QUERY_API, wxpayRequestXml);
        JSONObject wxpayResponseJson = XmlUtil.parseXmlJson(wxpayResponseXml);
        wxQueryresponse = (WxQueryResponse) JSONObject.toBean(wxpayResponseJson, WxQueryResponse.class);

        wxQueryresponse = (WxQueryResponse) WeChatCore.mkResponse(wxpayResponseJson, wxQueryresponse, wxpayKey);
        logger.info("微信查询返回:" + JSON.toJSONString(wxQueryresponse));

        return wxQueryresponse;

    }

}
