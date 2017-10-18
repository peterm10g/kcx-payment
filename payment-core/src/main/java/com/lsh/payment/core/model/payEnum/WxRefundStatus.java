package com.lsh.payment.core.model.payEnum;

/**
 * 微信退款状态
 */
public enum WxRefundStatus {

    WX_REFUND_ING("PROCESSING"),
    WX_REFUND_SUCCESS("SUCCESS"),
    WX_REFUND_CLOSE("REFUNDCLOSE"),
    WX_REFUND_EXCEPTION("CHANGE");

    private String name;

    WxRefundStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
