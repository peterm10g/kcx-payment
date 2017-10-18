package com.lsh.payment.api.model.baseVo;


import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/12/1.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class BaseContent implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -36582831901473766L;

    @JsonProperty("authed_amount")
    private String authedAmount;

    @JsonProperty("pay_payment_no")
    private String payPaymentNo;

    @JsonProperty("channel_name")
    private String channelName;

    @JsonProperty("pay_status")
    private Integer payStatus;

    public String getAuthedAmount() {
        return authedAmount;
    }

    public void setAuthedAmount(String authedAmount) {
        this.authedAmount = authedAmount;
    }

    public String getPayPaymentNo() {
        return payPaymentNo;
    }

    public void setPayPaymentNo(String payPaymentNo) {
        this.payPaymentNo = payPaymentNo;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }
}
