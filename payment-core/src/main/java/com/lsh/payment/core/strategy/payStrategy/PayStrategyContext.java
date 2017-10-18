package com.lsh.payment.core.strategy.payStrategy;


import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;

import java.util.Map;


/**
 * 支付策略上下文
 * Project Name: lsh-payment
 * Created by peter on 16/10/30.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class PayStrategyContext {

    private PayStrategy payStrategy;

    /**
     * 调用对应支付平台组装支付请求报文
     * @param payType
     * @param params
     * @return
     */
    public BasePayResponse generatePayParams(int payType,final Map<String, String> params) {
        payStrategy = PayStrategyFactory.getInstance().creator(payType);
        return payStrategy.generatePayParams(payType, params);
    }

}
