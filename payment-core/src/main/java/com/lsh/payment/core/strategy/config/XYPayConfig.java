package com.lsh.payment.core.strategy.config;

import com.lsh.payment.core.util.PropertiesUtil;
import org.springframework.stereotype.Component;

/**
 * Created by xuzhan on 16/10/25.
 */
@SuppressWarnings("all")
@Component
public class XYPayConfig {

    public static final String MAC_ID = "323";
    //后台服务通信密钥
    public static final String KEY = "32323";
    //支付
    public static final String PAY_API = "https://pay.swiftpass.cn/pay/gateway";
    //查询
    public static final String BILL_API = "https://download.swiftpass.cn/gateway";
    public static final String Bill_SERVICE = "pay.bill.bigMerchant";

    public static final String PX_NATIVE = "pay.weixin.native";
    public static final String PA_NATIVE = "pay.alipay.native";
    public static final String XY_QUERY = "unified.trade.query";

    public static final String Bill_TYPE = "SUCCESS";

    //支付成功
    public static final String TRADE_STATE_SUCCESS = "SUCCESS";
    //转入退款
    public static final String TRADE_STATE_REFUND = "REFUND";
    //未支付
    public static final String TRADE_STATE_NOTPAY = "NOTPAY";
    //已关闭
    public static final String TRADE_STATE_CLOSED = "CLOSED";
    //支付失败(其他原因，如银行返回失败)
    public static final String TRADE_STATE_PAYERROR = "PAYERROR";

    //商品名
    public static final String GOODS_NAME = "链商优供-支付";
    //微信扫码
    public static final String PAY_TYPE_WX = "800201";
    //支付宝扫码
    public static final String PAY_TYPE_ALI = "800101";

    public static final String XY_PAY_TITLE = "链商优供-支付(订单号:{0})";

    public static final String IP = "115.182.215.48";
    public static final String NOTIFY_URL = PropertiesUtil.getValue("xy.notify.url");
}
