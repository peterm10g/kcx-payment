package com.lsh.payment.core.service.alipay.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.StreamUtil;
import com.alipay.api.internal.util.StringUtils;
import com.alipay.api.internal.util.codec.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * Project Name: lsh-payment
 * Created by peter on 17/3/17.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class AlipayCheck {

    private static Logger logger = LoggerFactory.getLogger(AlipayCheck.class);

    public static boolean rsaCheckV1(Map<String, String> params, String publicKey, String charset) {
        String sign = (String)params.get("sign");
        String content = getSignCheckContentV1(params);
        System.out.println("content is " + content);

        boolean flag = false;
        try {
            flag = rsaCheckContent(content, sign, publicKey, charset);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return flag;
    }

    public static String getSignCheckContentV1(Map<String, String> params) {
        if(params == null) {
            return null;
        } else {
            params.remove("sign");
            params.remove("sign_type");
            StringBuffer content = new StringBuffer();
            ArrayList keys = new ArrayList(params.keySet());
            Collections.sort(keys);

            for(int i = 0; i < keys.size(); ++i) {
                String key = (String)keys.get(i);
                String value = (String)params.get(key);
                content.append((i == 0?"":"&") + key + "=" + value);
            }

            return content.toString();
        }
    }


    public static boolean rsaCheckContent(String content, String sign, String publicKey, String charset) throws AlipayApiException {
        try {
            PublicKey e = getPublicKeyFromX509("RSA", new ByteArrayInputStream(publicKey.getBytes()));
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initVerify(e);
            if(StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }

            return signature.verify(Base64.decodeBase64(sign.getBytes()));
        } catch (Exception var6) {
            throw new AlipayApiException("RSAcontent = " + content + ",sign=" + sign + ",charset = " + charset, var6);
        }
    }


    public static PublicKey getPublicKeyFromX509(String algorithm, InputStream ins) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        StringWriter writer = new StringWriter();
        StreamUtil.io(new InputStreamReader(ins), writer);
        byte[] encodedKey = writer.toString().getBytes();
        encodedKey = Base64.decodeBase64(encodedKey);
        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }


    //{"body":"介绍","subject":"链商订单:842641037733134336","sign_type":"RSA","buyer_logon_id":"liu***@gmail.com",
    // "auth_app_id":"2015111800831855","notify_type":"trade_status_sync","out_trade_no":"842641037733134336","version":"1.0",
    // "point_amount":"0.00","fund_bill_list":"[{\"amount\":\"0.01\",\"fundChannel\":\"ALIPAYACCOUNT\"}]","buyer_id":"2088802754006570",
    // "total_amount":"0.01","trade_no":"2017031721001004570245200554","notify_time":"2017-03-17 19:03:27","charset":"utf-8",
    // "invoice_amount":"0.01","trade_status":"TRADE_SUCCESS","gmt_payment":"2017-03-17 15:37:41",
    // "sign":"W5EEWIBQBRFt/CJRPNm9uiDstGFso2Ngo8uB8UuOTXB1gd1WTCNk1RPAafnQs0la8qtM4w+Shfy5uUYWikdr0EXnAfPPjtoUuwIBfOOcLr8y44K2v0DREV8UVKmyKM5Hh824xJ35/ulOMVuPo58+9Y2EaeFIPRznojuV2MY7UvQ=",
    // "gmt_create":"2017-03-17 15:37:40","buyer_pay_amount":"0.01","receipt_amount":"0.01","app_id":"2015111800831855","seller_id":"2088121287862492","seller_email":"lsh123_wx@sina.com",
    // "notify_id":"a51d4f45a370f44a3132e342689880ekee"}


    public static void main(String[] args) {
        Map params = new HashMap();
        params.put("body","介绍");
        params.put("subject","链商订单:842641037733134336");
//        params.put("sign_type","RSA");
//        params.put("buyer_logon_id","liu***@gmail.com");
//        params.put("auth_app_id","2015111800831855");
//        params.put("notify_type","trade_status_sync");
//        params.put("out_trade_no","842641037733134336");
//        params.put("version","1.0");
//        params.put("point_amount","0.00");
//        params.put("buyer_id","2088802754006570");
//        params.put("total_amount","0.01");
//        params.put("trade_no","2017031721001004570245200554");
//        params.put("notify_time","2017-03-17 19:03:27");
//        params.put("charset","utf-8");
//        params.put("invoice_amount","0.01");
//        params.put("trade_status","TRADE_SUCCESS");
//        params.put("gmt_payment","2017-03-17 15:37:41");
//        params.put("sign","W5EEWIBQBRFt/CJRPNm9uiDstGFso2Ngo8uB8UuOTXB1gd1WTCNk1RPAafnQs0la8qtM4w+Shfy5uUYWikdr0EXnAfPPjtoUuwIBfOOcLr8y44K2v0DREV8UVKmyKM5Hh824xJ35/ulOMVuPo58+9Y2EaeFIPRznojuV2MY7UvQ=");
//        params.put("gmt_create","2017-03-17 15:37:40");
//        params.put("buyer_pay_amount","0.01");
//        params.put("receipt_amount","0.01");
//        params.put("app_id","2015111800831855");
//        params.put("seller_id","2088121287862492");
//        params.put("seller_email","lsh123_wx@sina.com");
//        params.put("notify_id","a51d4f45a370f44a3132e342689880ekee");
////        params.put("fund_bill_list","[{\"fundChannel\":\"ALIPAYACCOUNT\",\"amount\":\"0.01\"}]");
//        params.put("fund_bill_list","[{\"amount\":\"0.01\",\"fundChannel\":\"ALIPAYACCOUNT\"}]");

        //System.out.println(rsaCheckV1(params, AliPayConfig.alipay_public_key,AliPayConfig.input_charset));

//        get(params);
    }


//    public static Map<String, String> get(Map paramsw){
//        Map<String,String> params = new HashMap<>();
//        Map requestParams = paramsw;
//        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
//            String name = (String) iter.next();
//            String[] values = (String[]) requestParams.get(name);
//            String valueStr = "";
//            for (int i = 0; i < values.length; i++) {
//                valueStr = (i == values.length - 1) ? valueStr + values[i]
//                        : valueStr + values[i] + ",";
//            }
//            //乱码解决，这段代码在出现乱码时使用。
//            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
//            params.put(name, valueStr);
//        }
//
//        return params;
//    }


}
