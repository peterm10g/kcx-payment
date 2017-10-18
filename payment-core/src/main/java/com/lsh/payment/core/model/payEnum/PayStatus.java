package com.lsh.payment.core.model.payEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付状态
 */
public enum PayStatus {
    CREATE_ERROR(0, "创建失败"),
    CREATE_PAYMENT(1, "创建支付订单"),
    PAYING(2, "支付中,待支付,已授权"),
    PAY_SUCCESS(3, "支付成功"),
    PAY_FAIL(4, "支付失败"),
//    PAY_CANCEL(5, "已取消"),
    PAY_CLOSE(6, "作废,关闭"),
    PAY_REFUND(10, "已退款");

    private int value;
    private String name;

    PayStatus(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static PayStatus getPayStatus(int status) {
        for (PayStatus it : PayStatus.values()) {
            if (it.getValue() == status) {
                return it;
            }
        }
        return null;
    }

    private static Map<Integer, String> map = new HashMap<Integer, String>(PayStatus.values().length);

    static {
        for (PayStatus payStatus : PayStatus.values()) {
            map.put(payStatus.getValue(), payStatus.getName());
        }
    }

    public static Map<Integer, String> getPayStatusMap() {
        return map;
    }
}
