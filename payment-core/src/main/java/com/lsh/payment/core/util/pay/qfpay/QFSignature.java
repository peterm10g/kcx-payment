package com.lsh.payment.core.util.pay.qfpay;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.lsh.payment.core.strategy.config.QFPayConfig;
import com.lsh.payment.core.util.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by miaozhuang
 * Date: 16/10/25
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.consumer.connector.utils.qfpay
 * desc:钱方签名
 */
public class QFSignature {
    private static Logger logger = LoggerFactory.getLogger(QFSignature.class);

    //生成签名字段
    public static String getSign(Map<String, Object> map) {
        String key = QFPayConfig.SERVER_CODE;
        ArrayList<String> list = new ArrayList<String>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
//            if (entry.getValue() != null && !entry.getValue().equals("")) {
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
//            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }
        sb.deleteCharAt(sb.length() - 1);
        String result = sb.toString();
        result += key;
        logger.info("Sign Before MD5:" + result);
        result = MD5Util.MD5Encode(result).toUpperCase();
        logger.info("Sign After MD5:" + result);
        return result;
    }


    //校验返回参数,验证签名字段
    public static boolean check(String rspBody, String rspSign) {
        String key = QFPayConfig.SERVER_CODE;
        String sign;
        if (!StringUtils.isBlank(rspBody)) {
            if (!StringUtils.isBlank(rspSign)) {
                sign = rspSign.split(":")[1].trim();
                rspBody = rspBody + key;
                logger.info("Sign Before MD5:" + rspBody);
                rspBody = MD5Util.MD5Encode(rspBody).toUpperCase();
                logger.info("Sign After MD5:" + rspBody);
                if (sign.equals(rspBody)) {
                    return true;
                }
                logger.error("钱方验签失败" + rspBody);
            }
        }
        return false;
    }
}
