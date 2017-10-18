package com.lsh.payment.core.strategy.queryStrategy;

import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;

import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/6.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public interface QueryStrategy<T extends BasePayResponse> {

    /**
     * 查询第三方支付状态
     * @param params 查询参数
     * @return
     */
    T queryPayStatusByParams(Map<String, Object> params);

}
