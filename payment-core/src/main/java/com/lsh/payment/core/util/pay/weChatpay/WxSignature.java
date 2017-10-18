package com.lsh.payment.core.util.pay.weChatpay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/10/25
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.consumer.connector.utils.qfpay
 * desc:微信签名
 */
public class WxSignature {

    private static Logger logger = LoggerFactory.getLogger(WxSignature.class);


    //生成签名字段
    public static String getNewSign(final Map<String, String> signMap, String key) {

        return SignUtils.createSign(signMap,key);
    }

//    public static String getNewSign(final Map<String, String> signMap, String key) {
//        ArrayList<String> list = new ArrayList<>();
//        for (Map.Entry<String, String> entry : signMap.entrySet()) {
//            if (StringUtils.isNotBlank(entry.getValue()) && ! entry.getKey().equals("sign")) {
//                list.add(entry.getKey() + "=" + entry.getValue() + "&");
//            }
//        }
//        int size = list.size();
//        String[] arrayToSort = list.toArray(new String[size]);
//        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < size; i++) {
//            sb.append(arrayToSort[i]);
//        }
//        sb.deleteCharAt(sb.length() - 1);
//        String result = sb.toString();
//        result = result + "&" + "key=" + key;
//        logger.info("wx sign before md5 : {}", result);
//        result = MD5.MD5Encode(result).toUpperCase();
//        logger.info("wx sign after md5 : {}", result);
//        return result;
//    }




}
