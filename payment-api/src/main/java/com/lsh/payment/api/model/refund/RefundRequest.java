package com.lsh.payment.api.model.refund;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Digits;
import java.io.Serializable;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/8.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RefundRequest implements Serializable{

    @NotBlank
    private String refund_trade_id;

    @NotBlank
    private String pay_payment_no;

    @NotBlank
    @Digits(integer = 8,fraction = 2,message = "支付金额不合法,小数点后最多两位,且大于0,例如(12.25,0.88)")
    private String refund_fee;

    private String notify_url;

    private String notify_ext;

    public String getPay_payment_no() {
        return pay_payment_no;
    }

    public void setPay_payment_no(String pay_payment_no) {
        this.pay_payment_no = pay_payment_no;
    }

    public String getRefund_fee() {
        return refund_fee;
    }

    public void setRefund_fee(String refund_fee) {
        this.refund_fee = refund_fee;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getNotify_ext() {
        return notify_ext;
    }

    public void setNotify_ext(String notify_ext) {
        this.notify_ext = notify_ext;
    }

    public String getRefund_trade_id() {
        return refund_trade_id;
    }

    public void setRefund_trade_id(String refund_trade_id) {
        this.refund_trade_id = refund_trade_id;
    }
}
