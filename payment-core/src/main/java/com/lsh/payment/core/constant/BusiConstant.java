package com.lsh.payment.core.constant;

import com.lsh.payment.core.util.PropertiesUtil;

import java.io.File;

public class BusiConstant {

    //微信
    public final static int WECHATPAY = 1;
    //微信查询
    public final static int WECHATQUERY = 2;
    //支付宝
    public final static int ALIPAY = 3;
    //支付宝查询
    public final static int ALIQUERY = 4;
    //钱方
    public final static int QFPAY = 5;
    //钱方查询
    public final static int QFQUERY = 6;
    //钱方
    public final static int XYPAY = 7;

    public final static int XYQUERY = 8;

    public final static int CMBCPAY = 9;

    public final static int CMBCQUERY = 10;

    public final static int ALLINPAY = 11;

    public final static int ALLINQUERY = 12;


    //操作日志
    //执行成功
    public final static int OPERATE_SUCCESS = 1;
    //执行失败
    public final static int OPERATE_FAIL = 0;
    //执行成功
    public final static String OPERATE_SUCCESS_STRING = "SUCCESS";
    //执行失败
    public final static String OPERATE_FAIL_STRING = "FAIL";

    //支付宝下载对账单参数
    public final static String ALIPAY_RESPONSE = "alipay_data_dataservice_bill_downloadurl_query_response";
    //签名
    public final static String ALIPAY_SIGN = "sign";
    //请求成功
    public final static String ALIPAY_CODE_SUCCESS = "10000";
    //下载地址
    public final static String DOWNLOAD_BILL_URL = "bill_download_url";


    //0-获取支付订单,1-收款回告,2-查询
    public final static int LOG_PAY_TYPE_PAY = 0;
    public final static int LOG_PAY_TYPE_PAY_NOTIFY = 1;
    public final static int LOG_PAY_TYPE_QUERY = 2;

//    public final static String HOST = "http://testpay.wmdev2.lsh123.com/";

    //支付平台回调业务方支付结果最大次数
    public final static Integer TMS_MAX_TIMES = 10;
    //添加回调任务初始状态
    public final static Integer TMS_TASK_STATUS_NEW = 0;
    //回调任务执行成功
    public final static Integer TMS_TASK_STATUS_SUCCESS = 1;
    //回调最大次数后不成功的,回调失败
    public final static Integer TMS_TASK_STATUS_FAIL = 2;

    //    public final static String BILL_DOWNLOAD_PATH = "/home/work/lsh-payment/bill";
    public final static String BILL_DOWNLOAD_PATH_ = "/Users/peter/logs";

    public final static String BILL_DOWNLOAD_PATH = PropertiesUtil.getValue("bill_download_path");

    public final static String BILL_DOWNLOAD_PATH_WX_GROUPON = BILL_DOWNLOAD_PATH + File.separator + "wxpayGroupon";
    public final static String BILL_DOWNLOAD_PATH_WX = BILL_DOWNLOAD_PATH + File.separator + "wxpay";
    public final static String BILL_DOWNLOAD_PATH_ALI = BILL_DOWNLOAD_PATH + File.separator + "alipay";
    public final static String BILL_DOWNLOAD_PATH_LKL = BILL_DOWNLOAD_PATH + File.separator + "lklpay";
    public final static String BILL_DOWNLOAD_PATH_QF = BILL_DOWNLOAD_PATH + File.separator + "qfpay";
    public final static String BILL_DOWNLOAD_PATH_XY = BILL_DOWNLOAD_PATH + File.separator + "xypay";
    public final static String BILL_DOWNLOAD_PATH_CMBC = BILL_DOWNLOAD_PATH + File.separator + "cmbcpay";

    //与第三方通信成功,(下单,查询接口)包括返回数据请求签名认证
    public final static String PAY_REQUEST_SUCCESS = "1";
    public final static String PAY_REQUEST_SUCCESS_MESSAGE = "SUCCESS";
    //与第三方通信失败(下单,查询接口)
    public final static String PAY_REQUEST_FAIL = "2";
    public final static String PAY_REQUEST_FAIL_MESSAGE = "FAIL";

    //支付宝下单Service名称
    public final static String PAY_ALI_SERVICE_NAME = "aliPayServiceImpl";
    //微信下单Service名称
    public final static String PAY_WX_SERVICE_NAME = "wxPayServiceImpl";
    //拉卡拉下单Service名称
    public final static String PAY_LKL_SERVICE_NAME = "lklPayServiceImpl";
    //钱方下单Service名称
    public final static String PAY_QF_SERVICE_NAME = "qfPayServiceImpl";

    //钱方下单Service名称
    public final static String QUERY_WX_SERVICE_NAME = "wxQueryServiceImpl";
    //钱方下单Service名称
    public final static String QUERY_QF_SERVICE_NAME = "qfQueryServiceImpl";
    //钱方下单Service名称
    public final static String QUERY_ALI_SERVICE_NAME = "aliQueryServiceImpl";
    //钱方下单Service名称
    public final static String QUERY_XY_SERVICE_NAME = "xyQueryServiceImpl";
    //钱方下单Service名称
    public final static String QUERY_CMBC_SERVICE_NAME = "cmbcQueryServiceImpl";

    public final static String QUERY_ALLIN_SERVICE_NAME = "allinQueryServiceImpl";

}
