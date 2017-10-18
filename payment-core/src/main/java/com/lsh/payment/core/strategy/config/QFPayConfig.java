package com.lsh.payment.core.strategy.config;

import com.lsh.payment.core.util.PropertiesUtil;
import org.springframework.stereotype.Component;

/**
 * Created by xuzhan on 16/10/25.
 */
@SuppressWarnings("all")
@Component
public class QFPayConfig {

    public static final String APP_CODE = PropertiesUtil.getValue("qf.app.code");
    //后台服务通信密钥
    public static final String SERVER_CODE = PropertiesUtil.getValue("qf.server_code");
    //支付
    public static final String PAY_API = PropertiesUtil.getValue("qf.api") + "/trade/v1/payment";
    //查询
    public static final String CHECK_API = PropertiesUtil.getValue("qf.api") + "/trade/v1/query";

    //商品名
    public static final String GOODS_NAME = "链商优供-支付";
    //微信扫码
    public static final String PAY_TYPE_WX = "800201";
    //支付宝扫码
    public static final String PAY_TYPE_ALI = "800101";
    //notify_type
    public static final String ORDER_TYPE_PAYMENT = "payment";
    public static final String RMB = "CNY";
    public static final String CANCLE_NOMAL = "0";
    //    status
    public static final String CALL_BACK_SUCCESS = "1";
    public static final String HEAD_SIGN = "X-QF-SIGN";

    //0000表示交易成功； 1143、1145表示交易中，需要继续查询交易结果； 其他返回码表示交易失败
    public static final String RESPCD_SUCCESS = "0000";
    public static final String RESPCD_TRADEING_1 = "1143";
    public static final String RESPCD_TRADEING_2 = "1145";

    public static final String QF_PAY_TITLE = "链商优供-支付(订单号:{0})";

    public static final String QUERY_PAGE_SIZE = "100";
}
