package com.lsh.payment.core.util.pay.cmbcpay;

import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ObjectAndMapConvert {

	public static Map<String, String> convertObjectToMap(Object obj) {
        if(obj == null){    
            return null;    
        }

		Map<String, String> map = new HashMap<>();
		try {
			Field[] declaredFields = obj.getClass().getDeclaredFields();
			for (Field field : declaredFields) {
                field.setAccessible(true);
                map.put(field.getName(), (String)field.get(obj));
            }
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return removeEmptyData(map);
    }  
	
	public static Map<String, String> removeEmptyData(Map<String, String> sourceMap) {
		Map<String,String> resultMap = new HashMap<>();
		for (Iterator<String> it = sourceMap.keySet().iterator(); it.hasNext();) {
			String key = it.next();
			String value = sourceMap.get(key);
			if(StringUtils.isEmpty(value)){
				continue;
			}
			resultMap.put(key, value);
		}
		
		return resultMap;
	}
	
	
}
