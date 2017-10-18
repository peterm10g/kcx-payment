package com.lsh.payment.core.strategy.config;

import com.lsh.payment.core.util.PropertiesUtil;
import org.springframework.stereotype.Component;



@Component
public class WxPayConfig {
    // =======【基本信息设置】=====================================
    /*
     * 微信公众号信息配置 APPID：绑定支付的APPID（必须配置） MCHID：商户号（必须配置）
	 * KEY：商户支付密钥，参考开户邮件设置（必须配置） APPSECRET：公众帐号secert（仅JSAPI支付的时候需要配置）
	 */
    public static final String NOTIFY_URL = PropertiesUtil.getValue("wx.notify.url");
    public static final String TRADE_TYPE = "JSAPI";

    //环境
    public static final String APPID = PropertiesUtil.getValue("wx.appid");
    public static final String MCHID = PropertiesUtil.getValue("wx.mchid");
    public static final String KEY = PropertiesUtil.getValue("wx.key");
    public static final String APPSECRET = PropertiesUtil.getValue("wx.appsecret");

    public static final String GROUPON_APPID = PropertiesUtil.getValue("wx.appid.g");
    public static final String GROUPON_MCHID  = PropertiesUtil.getValue("wx.mchid.g");
    public static final String GROUPON_KEY = PropertiesUtil.getValue("wx.key.g");
    public static final String GROUPON_APPSECRET = PropertiesUtil.getValue("wx.appsecret.g");

    /*
     * 微信APP信息配置 APPID：绑定支付的APPID（必须配置） MCHID：商户号（必须配置）
	 * KEY：商户支付密钥，参考开户邮件设置（必须配置） APPSECRET：公众帐号secert（仅JSAPI支付的时候需要配置）
	 */
    //生产环境
    public static final String NOTIFY_URL_APP = PropertiesUtil.getValue("wx.notify.url");
    public static final String TRADE_TYPE_APP = "APP";

    public static final String APPID_APP = "wx8ed7a894512766dd";
    public static final String MCHID_APP = "1288575201";
    public static final String KEY_APP = "Lianshangyougong123weixinzhifu16";

    //qf
    public static final String APPID_NATIVE = "wxa25e306362e511c6";
    public static final String MCHID_NATIVE = "10028073";//"1380355902";
    public static final String KEY_NATIVE = "50eb2fa9327fc9e2a3c91876bfab14a1";


    public static final String IP = "115.182.215.48";
    public static final String GOODS_NAME = "链商优供-支付";
    public static final String GROUPON_GOODS_NAME = "邻选-支付";

    public static final String PAY_API = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    public static final String QUERY_API = "https://api.mch.weixin.qq.com/pay/orderquery";

    public static final String REFUND_API = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    public static final String REFUND_QUERY_API = "https://api.mch.weixin.qq.com/pay/refundquery";
//    https://api.mch.weixin.qq.com/secapi/pay/refund
//    https://api.mch.weixin.qq.com/pay/refundquery

    public static final String SUCCESS_CODE = "SUCCESS";

//    public static final String RETURN_URL = "http://m.yougong.elianshang.com/my/order";

    public static final String PAY_TITLE_NAME = "支付订单:{0}";

    public static final String WX_APP_PACKAGE = "Sign=WXPay";

    public static final String WX_H5_PACKAGE = "prepay_id={0}";

    public static final String WX_H5_SIGNTYPE = "MD5";

    //SUCCESS—支付成功 ,REFUND—转入退款 ,NOTPAY—未支付 ,CLOSED—已关闭
    //REVOKED—已撤销（刷卡支付）,USERPAYING--用户支付中 ,PAYERROR--支付失败(其他原因，如银行返回失败)
    public static final String WX_SUCCESS = "SUCCESS";
    public static final String WX_NOTPAY = "NOTPAY";
    public static final String WX_CLOSED = "CLOSED";
    public static final String WX_USERPAYING = "USERPAYING";
    public static final String WX_PAYERROR = "PAYERROR";

    public static final String WX_H5_BILL_NAME = "wx_h5_bill_{0}.txt";

    public static final String WX_APP_BILL_NAME = "wx_app_bill_{0}.txt";

    public static final String WXSM_QF_BILL_NAME = "qf_wxsm_bill_{0}.txt";
    public static final String ALISM_QF_BILL_NAME = "qf_alism_bill_{0}.txt";

    public static final String WXSM_XY_BILL_NAME = "xy_wxsm_bill_{0}.txt";
    public static final String ALISM_XY_BILL_NAME = "xy_alism_bill_{0}.txt";

    public static final String WX_BILL_DOWNLOAD_URL = "https://api.mch.weixin.qq.com/pay/downloadbill";


    public static final String KEY_PATH=PropertiesUtil.getValue("wx.perm.g");
//            "/Users/peter/groupon/cert_test/apiclient_cert.p12";

//    public static final String KEY_PATH="/Users/peter/groupon/cert_pro/apiclient_cert.p12";

}
