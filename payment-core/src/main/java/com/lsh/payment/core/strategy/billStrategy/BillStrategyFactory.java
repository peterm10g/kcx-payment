package com.lsh.payment.core.strategy.billStrategy;

import com.lsh.payment.core.model.payEnum.PayChannel;

import java.util.HashMap;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/24.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class BillStrategyFactory {

    private static Map<String, BillStrategy> strategyMap = new HashMap<>();

    static {

        strategyMap.put(PayChannel.WXPAY.getName(), new WxBillStrategy());
        strategyMap.put(PayChannel.ALIPAY.getName(), new AliBillStrategy());

    }

    private BillStrategyFactory() {
    }

    private static class InstanceHolder {
        public static BillStrategyFactory instance = new BillStrategyFactory();
    }

    public static BillStrategyFactory getInstance() {
        return BillStrategyFactory.InstanceHolder.instance;
    }

    public BillStrategy creator(String type) {
        return strategyMap.get(type);
    }
}
