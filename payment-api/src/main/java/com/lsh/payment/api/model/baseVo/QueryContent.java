package com.lsh.payment.api.model.baseVo;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/12/1.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class QueryContent implements Serializable {

    @JsonProperty("channel_transaction")
    private String channelTransaction;
    @JsonProperty("trade_id")
    private String tradeId;
    @JsonProperty("pay_code")
    private Integer payCode;
    @JsonProperty("pay_msg")
    private String payMsg;


    public String getChannelTransaction() {
        return channelTransaction;
    }

    public void setChannelTransaction(String channelTransaction) {
        this.channelTransaction = channelTransaction;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public Integer getPayCode() {
        return payCode;
    }

    public void setPayCode(Integer payCode) {
        this.payCode = payCode;
    }

    public String getPayMsg() {
        return payMsg;
    }

    public void setPayMsg(String payMsg) {
        this.payMsg = payMsg;
    }
}
