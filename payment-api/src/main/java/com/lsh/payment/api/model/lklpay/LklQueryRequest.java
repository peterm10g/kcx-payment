package com.lsh.payment.api.model.lklpay;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/16.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LklQueryRequest implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 7655869214382345747L;

    @NotBlank
    private String payPaymentNo;

    @NotBlank
    private String channelTransaction;

    @NotBlank
    private String receiveAmount;


    public String getPayPaymentNo() {
        return payPaymentNo;
    }

    public void setPayPaymentNo(String payPaymentNo) {
        this.payPaymentNo = payPaymentNo;
    }

    public String getChannelTransaction() {
        return channelTransaction;
    }

    public void setChannelTransaction(String channelTransaction) {
        this.channelTransaction = channelTransaction;
    }

    public String getReceiveAmount() {
        return receiveAmount;
    }

    public void setReceiveAmount(String receiveAmount) {
        this.receiveAmount = receiveAmount;
    }
}
