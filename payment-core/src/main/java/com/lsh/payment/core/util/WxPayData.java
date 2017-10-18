package com.lsh.payment.core.util;

import com.lsh.payment.core.exception.BusinessException;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Project Name: lsh-payment
 * Created by miaozhuang on 16/10/26.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class WxPayData {

	private static Logger Log = Logger.getLogger(WxPayData.class);

	public WxPayData() {

	}

	// 采用排序的Dictionary的好处是方便对数据包进行签名，不用再签名之前再做一次排序
	private TreeMap<String, Object> m_values = new TreeMap<String, Object>();

	/**
	 * 设置某个字段的值
	 * 
	 * @param key
	 *            字段名
	 * @param value
	 *            字段值
	 */
	public void SetValue(String key, Object value) {
		m_values.put(key, value);
	}

	/**
	 * 根据字段名获取某个字段的值
	 * 
	 * @param key
	 *            字段名
	 * @return key对应的字段值
	 */
	public Object GetValue(String key) {
		return m_values.get(key);
	}

	/**
	 * 判断某个字段是否已设置
	 * 
	 * @param key
	 *            字段名
	 * @return 若字段key已被设置，则返回true，否则返回false
	 */
	public boolean IsSet(String key) {
		return m_values.containsKey(key);
	}



	public String toUrl() throws BusinessException, UnsupportedEncodingException {
		String resBuff = "";
		StringBuffer buff = new StringBuffer();
		for (Entry<String, Object> pair : m_values.entrySet()) {
			if (pair.getValue() == null) {
				Log.error("WxPayData内部含有值为null的字段!");
				throw new BusinessException();
			}

//			if (!pair.getKey().equalsIgnoreCase("sign") && !pair.getValue().toString().equalsIgnoreCase("")) {
//				buff += pair.getKey() + "=" + pair.getValue().toString() + "&";
//			}
			if (!pair.getKey().equalsIgnoreCase("sign") && !pair.getValue().toString().equalsIgnoreCase("")) {
				buff.append(pair.getKey()).append("=").append(pair.getValue().toString()).append("&");
			}
		}
		resBuff = buff.toString();
		String regpattern = "&+$";
		Pattern pattern = Pattern.compile(regpattern, Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(resBuff);
		if (m.find()) {
			resBuff = resBuff.substring(0, buff.length() - 1);
		}
		return resBuff;
	}



	/**
	 * @获取Dictionary
	 */
	public TreeMap<String, Object> GetValues() {
		return m_values;
	}
}
