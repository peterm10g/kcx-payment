package com.lsh.payment.core.model.payEnum;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/26.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public enum PayChannel {

    ALIPAY("alipay", 1), WXPAY("wxpay", 2), LKLPAY("lklpay", 5), QFPAY("qfpay", 6),WXPAYGROUPON("wxpaygroupon", 20),
    XYPAY("xypay", 16),CMBCPAY("cmbcpay", 17),ALLINPAY("allinpay", 18);

    private String name;

    private int code;

    PayChannel(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public static PayChannel getPayChannel(String name) {
        for (PayChannel it : PayChannel.values()) {
            if (it.getName().equals(name)) {
                return it;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

}
