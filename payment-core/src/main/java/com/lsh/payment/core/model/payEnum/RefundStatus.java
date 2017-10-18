package com.lsh.payment.core.model.payEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 退款状态
 */
public enum RefundStatus {
    REFUND_NEW(1, "新建"),
    REFUND_ING(2, "申请成功  退款中"),
    REFUND_SUCCESS(3, "退款成功"),
    REFUND_CLOSE(4, "退款关闭"),
    REFUND_EXCEPTION(5, "退款异常");

    private int value;
    private String name;

    RefundStatus(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static RefundStatus getPayStatus(int status) {
        for (RefundStatus it : RefundStatus.values()) {
            if (it.getValue() == status) {
                return it;
            }
        }
        return null;
    }

    private static Map<Integer, String> map = new HashMap<Integer, String>(RefundStatus.values().length);

    static {
        for (RefundStatus payStatus : RefundStatus.values()) {
            map.put(payStatus.getValue(), payStatus.getName());
        }
    }

    public static Map<Integer, String> getPayStatusMap() {
        return map;
    }
}
