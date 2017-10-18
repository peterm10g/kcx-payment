package com.lsh.payment.core.util;

import com.lsh.payment.core.strategy.config.WxPayConfig;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.net.URLEncoder;
import java.util.Map;


/**
 * Project Name: lsh-payment
 * Created by miaozhuang on 16/10/26.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class JsApiPay {

	private static Logger Log = Logger.getLogger(JsApiPay.class);

	/**
	 *
	 * 第一步：利用url跳转获取code
	 * 第二步：利用code去获取openid和access_token
	 *
	 * @throws Exception
	 *
	 */
	public static void requestOpenId(String host,String key) throws Exception {
		//host = "http://127.0.0.1:8888/api/payment/v1/wxpay/setCode";

		String redirect_uri = URLEncoder.encode(host, "UTF-8");
		WxPayData data = new WxPayData();
//		data.SetValue("appid", WxPayConfig.APPID);
		data.SetValue("appid", "wxa25e306362e511c6");
		data.SetValue("redirect_uri", redirect_uri);
		data.SetValue("response_type", "code");
		data.SetValue("scope", "snsapi_base");
		data.SetValue("state", key);//TODO redis key

		System.out.println("data.toUrl() is " + data.toUrl());

		String params = data.toUrl() + "#wechat_redirect";

		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?" + params;//TODO 配置

		System.out.println("Will Redirect to URL : " + url);

		String res = HttpClientUtils.doGet(url);

		System.out.println("res is " + res);

	}

	public static void main(String[] args) {
		try {
			requestOpenId("http://testpay.wmdev2.lsh123.com/pay/wxpay/setCode","test");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * 通过code换取网页授权access_token和openid的返回数据，
	 * openid是微信支付jsapi支付接口统一下单时必须的参数
	 * 更详细的说明请参考网页授权获取用户基本信息：
	 *
	 * @throws
	 *
	 */
	public static String getOpenId(String code) throws Exception {
		Log.debug("Get code : " + code);
		WxPayData data = new WxPayData();
		data.SetValue("appid", WxPayConfig.APPID);
		data.SetValue("secret", WxPayConfig.APPSECRET);
		data.SetValue("code", code);
		data.SetValue("grant_type", "authorization_code");
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" + data.toUrl();

		// 请求url以获取数据
		String result = HttpClientUtils.doGet(url);

		Log.debug("GetOpenidAndAccessTokenFromCode response : " + result);

		// 保存access_token，用于收货地址获取
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> jd = mapper.readValue(result, Map.class);

		String access_token = jd.get("access_token");

		// 获取用户openid
		String openid = jd.get("openid");

		Log.debug("Get openid : " + openid);
		Log.debug("Get access_token : " + access_token);

		return openid;
	}




}
