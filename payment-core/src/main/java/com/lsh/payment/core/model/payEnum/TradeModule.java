package com.lsh.payment.core.model.payEnum;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/26.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public enum TradeModule {

    ORDER("order"),BILL("bill"),RECYCLE("recycle");

    private String name;

    TradeModule(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
