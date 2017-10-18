package com.lsh.payment.core.strategy.queryStrategy;

import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;
import org.slf4j.Logger;

import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/6.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class QueryStrategyContext {

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(QueryStrategyContext.class);

    private QueryStrategy queryStrategy;


    /**
     * 调用对应支付平台组装支付请求报文
     *
     * @param payType 类型
     * @param params 参数
     * @return
     */
    public BasePayResponse queryPayStatusByParams(int payType, Map<String, Object> params) {

        queryStrategy = QueryStrategyFactory.getInstance().creator(payType);

        return queryStrategy.queryPayStatusByParams(params);
    }



}
