package com.lsh.payment.core.model.payEnum;

/**
 * Project Name: lsh-payment
 * Created by peter on 17/7/11.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public enum IsValid {

    VALID_TRUE(1, "有效"),
    VALID_FALSE(0, "无效");

    private int value;
    private String name;

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    IsValid(int value, String name) {
        this.value = value;
        this.name = name;
    }
}
