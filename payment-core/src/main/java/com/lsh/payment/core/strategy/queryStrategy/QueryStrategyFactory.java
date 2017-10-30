package com.lsh.payment.core.strategy.queryStrategy;

import com.lsh.payment.core.constant.BusiConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/6.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class QueryStrategyFactory {

    private static Map<Integer, QueryStrategy> strategyMap = new HashMap<>();

    static {

        strategyMap.put(BusiConstant.WECHATQUERY, new WxQueryStrategy());
        strategyMap.put(BusiConstant.ALIQUERY, new AliQueryStrategy());
    }

    private QueryStrategyFactory() {
    }

    private static class InstanceHolder {
        public static QueryStrategyFactory instance = new QueryStrategyFactory();
    }

    public static QueryStrategyFactory getInstance() {
        return InstanceHolder.instance;
    }

    public QueryStrategy creator(Integer type) {
        return strategyMap.get(type);
    }
}
