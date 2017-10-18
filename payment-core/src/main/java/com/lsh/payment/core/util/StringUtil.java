package com.lsh.payment.core.util;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/11/1
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.util
 * desc:
 */
public class StringUtil {
    /**
     * json字符串数据转化成Map
     *
     * @param json String
     *
     * @return json对应的map
     *
     * **/
    public static HashMap<String, Object> toMap(String json) {
        HashMap<String, Object> data = new HashMap<>();
        // 将json字符串转换成jsonObject
        JSONObject jsonObject = JSONObject.parseObject(json);
        // 遍历jsonObject数据，添加到Map对象
        for (String key : jsonObject.keySet()) {
            String value = jsonObject.getString(key);
            data.put(key, value);
        }
        return data;
    }
}
