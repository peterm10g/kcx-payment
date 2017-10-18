package com.lsh.payment.api.model.refund;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/8.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RefundQueryRequest implements Serializable{

    @NotBlank
    private String refund_trade_id;

//    @NotBlank
//    @Digits(integer = 8,fraction = 2,message = "支付金额不合法,小数点后最多两位,且大于0,例如(12.25,0.88)")
//    private String refund_fee;


    public String getRefund_trade_id() {
        return refund_trade_id;
    }

    public void setRefund_trade_id(String refund_trade_id) {
        this.refund_trade_id = refund_trade_id;
    }
}
