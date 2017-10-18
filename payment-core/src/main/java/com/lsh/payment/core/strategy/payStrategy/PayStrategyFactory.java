package com.lsh.payment.core.strategy.payStrategy;

import com.lsh.payment.core.constant.BusiConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付策略工厂
 * Project Name: lsh-payment
 * Created by peter on 16/10/30.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class PayStrategyFactory {

    private static Map<Integer, PayStrategy> strategyMap = new HashMap<>();

    static {
        strategyMap.put(BusiConstant.ALIPAY, new AlipayStrategy());
        strategyMap.put(BusiConstant.WECHATPAY, new WxPayStrategy());
        strategyMap.put(BusiConstant.QFPAY, new QfPayStrategy());
        strategyMap.put(BusiConstant.XYPAY, new XyPayStrategy());
        strategyMap.put(BusiConstant.CMBCPAY, new CMBCPayStrategy());
        strategyMap.put(BusiConstant.ALLINPAY, new AllinPayStrategy());
    }

    private PayStrategyFactory() {
    }

    private static class InstanceHolder {
        public static PayStrategyFactory instance = new PayStrategyFactory();
    }

    public static PayStrategyFactory getInstance() {
        return InstanceHolder.instance;
    }

    public PayStrategy creator(Integer type) {
        return strategyMap.get(type);
    }

}
