package com.lsh.payment.core.strategy.payStrategy;


import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;

import java.util.Map;


/**
 * 支付策略路由
 * Project Name: lsh-payment
 * Created by peter on 16/10/30.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public interface PayStrategy<T extends BasePayResponse> {

    /**
     * 调用对应支付平台组装支付请求报文
     * @param payType 传入需要的支付方式
     * @param params  其他额外需要的参数
     * @return 生成的支付请求
     */
    T generatePayParams(int payType, final Map<String, String> params);

}
