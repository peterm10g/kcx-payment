package com.lsh.payment.core.model.payEnum;

/**
 * JSAPI 微信公众号， 微信浏览器支付()
 * 业务方调用的时候，传以下3中类型即可，payService中对自动把WAP根据需要解析成JSAPI：
 * NATIVE 扫码
 * App  app支付
 * WAP 浏览器，网页版支付
 * 微信支付类型：
 * https://pay.weixin.qq.com/wiki/doc/api/micropay.php?chapter=4_2
 */
public enum BillTradeType {

    JSAPI(1, "JSAPI"), WXNATIVE(2, "WXNATIVE"), APP(3, "APP"), WAP(4, "WAP"),
    MICROPAY(5, "MICROPAY"), ALINATIVE(6, "ALINATIVE");

    private int code;

    private String name;

    BillTradeType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static BillTradeType getTradeTypeCode(int code) {
        for (BillTradeType billTradeType : BillTradeType.values()) {
            if (billTradeType.getCode() == code) {
                return billTradeType;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
