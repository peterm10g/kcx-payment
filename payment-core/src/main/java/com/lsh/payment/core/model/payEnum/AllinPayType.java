package com.lsh.payment.core.model.payEnum;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/30.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public enum AllinPayType {

    WXSM("W01","微信扫码支付"),
    ALISM("A01","支付宝扫码支付");

    private String code;

    private String name;

    AllinPayType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
