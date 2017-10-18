package com.lsh.payment.core.strategy.config;

import com.lsh.payment.core.util.PropertiesUtil;
import org.springframework.stereotype.Component;

/**
 * Created by xuzhan on 16/10/25.
 */
@SuppressWarnings("all")
@Component
public class AliPayConfig {

    // 合作身份者ID，签约账号，以2088开头由16位纯数字组成的字符串，查看地址：https://b.alipay.com/order/pidAndKey.htm
    public static final String partner = "2088121287862492";

    public static final String app_id = "2015111800831855";

    // 收款支付宝账号，以2088开头由16位纯数字组成的字符串，一般情况下收款账号就是签约账号
    public static final String seller_id = partner;

    public static final String seller_id_new = "lsh123_wx@sina.com";

    //商户的私钥,需要PKCS8格式，RSA公私钥生成：https://doc.open.alipay.com/doc2/detail.htm?spm=a219a.7629140.0.0.nBDxfy&treeId=58&articleId=103242&docType=1
    public static final String private_key = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKZfq5WKE/ZGXV9+\n" +
            "KExpkFeei7WasoVA4A8zavB974Z4sdhq9BHnLC9qlKPdOBVoSTZlaOZrC17Fm7YZ" +
            "aB2vKhCXAIG3ofI99L9rmefL8vWcCx4k6GmAp3DNCAzCUmljGK+kXHP1+w2wavqV" +
            "o6OsNXGPS+dxxw4FIgVuORqY5NLFAgMBAAECgYAXHd/nbUIMzAYZSJws0dYedocO" +
            "+qmnXjZDpm9LbxQi6Q489c9n1WkMRZDVm905DD5v8nM64NC5oFdcW/ddeIMthZBQ" +
            "Xp5F/46ZFHAzQqbC4FnP999x2qozZDELjiiTLNibasmndEX3BX5IWjY4CJcFMxjR" +
            "UUIypj69ST9tS2Q5wQJBANLznq2evQin4e2JkWXdQz8GgvT172j33wn3x5xSkX8V" +
            "kXXwaESP2YexepDLMetMtY0lFoZYD1ouKFQxaua5/MkCQQDJ5wxW6Zt9QO1gHYDF" +
            "DQ0U+Obu9zUn0kJ4qdUOCw0Y7XaWL7UpiQYRhcagLwbJpp4mFHl8LLTGA8n8J1Oh" +
            "QbAdAkA1WukHgN7PEadTLThZS112027MBmhHZGpFWyZho4CpZAsmiWfV74xVhc46" +
            "USqPGRfSW08XK662YHZS1Sz0rpYBAkEAvaW4Un8V3Z46Gjk8Nlue+R8fFEHCfUgj" +
            "xeGIzasVv192L3Zajcw2lgj5XIcvsgQ+svgycLAxkXoHpUFvbZ4dBQJAX1YKMXTF" +
            "tTfYnokXDNY56cQnHhSEso7LW9i/DTVzJmo7Xj7DvvC5cpQubaFC1ZtEpikZ7Vzr" +
            "hkwF8RQnOUfjag==";
    //合作者支付宝公钥
    public static final String public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRA" +
            "FljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQE" +
            "B/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5Ksi" +
            "NG9zpgmLCUYuLkxpLQIDAQAB";

    //应用支付宝的公钥,查看地址：https://b.alipay.com/order/pidAndKey.htm
    public static final String alipay_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUe" +
            "JHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRG" +
            "i60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static final String notify_url = PropertiesUtil.getValue("ali.notify.url");
            //PropertiesUtil.getValue("ali.notify.url");

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static final String return_url = "http://hx.market-h5.wmdev2.lsh123.com/#my/order/comment?order_id=";

    // 用户付款中途退出返回商户网站的地址。 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    // public static String show_url = "http://商户网址/create_direct_pay_by_user-JAVA-UTF-8/return_url.jsp";

    // 签名方式
    public static final String sign_type = "RSA";

    // 字符编码格式 目前支持 gbk 或 utf-8
    public static final String input_charset = "utf-8";

    // 支付类型 ，无需修改
    public static final String payment_type = "1";

    // 调用的接口名，无需修改
    public static final String service = "alipay.wap.create.direct.pay.by.user";

    // 防钓鱼时间戳  若要使用请调用类文件submit中的query_timestamp函数
    public static final String anti_phishing_key = "";

    // 客户端的IP地址 非局域网的外网IP地址，如：221.0.0.1
    public static final String exter_invoke_ip = "";

    // 设置未付款交易的超时时间
    // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
    // 取值范围：1m～15d。
    // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
    // 该参数数值不接受小数点，如1.5h，可转换为90m。
    public static final String it_b_pay = "20m";

    /**
     * 支付宝提供给商户的服务接入网关URL(新)
     */
    public static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do";

    public static final String ALIPAY_QUERY_API = "https://openapi.alipay.com/gateway.do";

    public static final String ALIPAY_DOWNLOAD_API = "https://openapi.alipay.com/gateway.do";

    public static final String GOODS_NAME = "链商优供-支付";

    public static final String GOODS_APP_NAME = "链商订单:{0}";

    public static final String GOODS_APP_DETAIL = "介绍";

    public static final String PRODUCT_CODE = "QUICK_MSECURITY_PAY";

    public static final String TIMEOUT_EXPRESS = "30m";

    public static final String SUCCESS_CODE = "10000";

    //WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
    public static final String TRADE_SUCCESS = "TRADE_SUCCESS";
    public static final String TRADE_CLOSED = "TRADE_CLOSED";
    public static final String TRADE_FINISHED = "TRADE_FINISHED";
    public static final String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
    //对账单下载
    public static final String DOWNLOAD_BILL_TYPE = "trade";

    public static final String CHANNEL_NAME = "alipay";

    public static final String ALIPAY_TITLE = "支付订单:{0}";

    public static final String notify_type = "trade_status_sync";
    public static final String notify_service = "notify_verify";

    public static final String ALI_BILL_ZIP_NAME = "ali_bill_{0}.zip";

}
