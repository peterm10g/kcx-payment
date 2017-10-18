package com.lsh.payment.core.constant;


public class RedisKeyConstant {


    /**
     * redis的key命名
     */
    //各接口被调频率
    public static final String CALLED_RATE = "pay:monitor:called:rate:{0}:{1}";
    //接口连续失败次数
    public static final String FAIL_NUM = "pay:monitor:fail:num:{0}";
    //接口失败频率
    public static final String FAIL_RATE = "pay:monitor:fail:rate:{0}:{1}";
    //接口处理时间过长
    public static final String HANDLE_LONG = "pay:monitor:handle:long:{0}";
    //邮件
    public static final String EMAIL = "pay:monitor:email";
    //支付重复下单锁
    public static final String PAY_TRADE_LOCK = "pay:trade:lock:{0}";
    //拉卡拉秘钥key
    public static final String LKL_KEY = "pay:lkl:perm:key";
    //钱方查询的key
    public static final String QF_QUERY_KEY = "pay:qf:query:key:{0}";

    public static final String XY_QUERY_KEY = "pay:xy:query:key:{0}";

    //支付重复下单锁
    public static final String PAY_REFUND_TRADE_LOCK = "pay:trade:refund:lock:{0}";

    public static final String PAY_TRADE_TIMES = "pay:trade:times";

    public static final String PAY_TRADE_SM_CHANNEL = "pay:trade:sm:channel:{0}";


}
