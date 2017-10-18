package com.lsh.payment.core.model.payEnum;

/**
 * 支付类型
 */
public enum PayType {

    PAY(1, "pay");

    private int code;
    private String name;

    PayType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }


    public String getName() {
        return name;
    }

}
