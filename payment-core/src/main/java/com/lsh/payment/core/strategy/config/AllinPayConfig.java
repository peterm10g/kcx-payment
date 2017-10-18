package com.lsh.payment.core.strategy.config;

import com.lsh.payment.core.util.PropertiesUtil;
import org.springframework.stereotype.Component;

/**
 * Created by xuzhan on 16/10/25.
 */
@SuppressWarnings("all")
@Component
public class AllinPayConfig {

    public static final String CUS_ID = "142581072993330";
    public static final String APP_ID = "00008692";
    //后台服务通信密钥
    public static final String KEY = "1234567890";
    //支付  00008692
    public static final String PAY_BASE = "http://vsp.allinpay.com/apiweb/unitorder";
    //查询
    public static final String BILL_API = "https://download.swiftpass.cn/gateway";
    public static final String Bill_SERVICE = "pay.bill.bigMerchant";

    public static final String PAY_NATIVE = "/pay";
    public static final String PAY_QUERY = "/order";

    public static final String Bill_TYPE = "SUCCESS";

    //支付成功
    public static final String TRADE_STATE_SUCCESS = "0000";
    public static final String PAY_STATE_SUCCESS = "0000";
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

    public static final String ALLIN_PAY_TITLE = "链商优供-支付(订单号:{0})";

    public static final String IP = "115.182.215.48";
    public static final String NOTIFY_URL = PropertiesUtil.getValue("allin.notify.url");

    // 00	微信
    public static final String PAY_CHANNEL_WX = "00";
    //01	支付宝
    public static final String PAY_CHANNEL_ALI = "01";

    //拉卡拉ftp文件名 partern
    public static final String CMBC_FTP_FILENAME = "cpos_yf_0199980301_{0}_jn_{1}.txt";
    //拉卡拉下载文件名
    public static final String CMBC_LOCAL_FILENAME = "cmbc_bill_{0}_{1}.txt";
    //拉卡拉SFTP host
    public static final String SFTP_HOST = "sfftp.lakala.com";
    //
    public static final int SFTP_PORT = 2022;
    //用户名
    public static final String SFTP_USERNAME = "lsh";
    //密码
    public static final String SFTP_PWD = "lsh@1114";



}
