package com.lsh.payment.api.model.payment;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/10.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentQueryRequest implements Serializable {

    private String trade_id;

    private String pay_payment_no;

    private String channel_transaction;

    public String getTrade_id() {
        return trade_id;
    }

    public void setTrade_id(String trade_id) {
        this.trade_id = trade_id;
    }

    public String getPay_payment_no() {
        return pay_payment_no;
    }

    public void setPay_payment_no(String pay_payment_no) {
        this.pay_payment_no = pay_payment_no;
    }

    public String getChannel_transaction() {
        return channel_transaction;
    }

    public void setChannel_transaction(String channel_transaction) {
        this.channel_transaction = channel_transaction;
    }
}
