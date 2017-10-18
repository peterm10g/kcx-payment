package com.lsh.payment.core.model.PayTask;

import java.util.Date;


@edu.umd.cs.findbugs.annotations.SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2", "EI_EXPOSE_REP2"})
public class PayTask {

    private Long id;
    /**
     * 支付平台订单号
     */
    private String payId;
    /**
     * 支付平台流水号
     */
    private String payPaymentNo;
    /**
     * 第三方支付订单号
     */
    private String channelTransaction;
    /**
     * 业务方订单号
     */
    private String tradeId;
    /**
     * 业务平台支付类型
     */
    private String tradeModule;

    private Date createdAt;

    private Date updatedAt;
    /**
     * 查询第三方支付结果次数
     */
    private Integer queryTimes;

    private Integer emailStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId == null ? null : payId.trim();
    }

    public String getPayPaymentNo() {
        return payPaymentNo;
    }

    public void setPayPaymentNo(String payPaymentNo) {
        this.payPaymentNo = payPaymentNo == null ? null : payPaymentNo.trim();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getChannelTransaction() {
        return channelTransaction;
    }

    public void setChannelTransaction(String channelTransaction) {
        this.channelTransaction = channelTransaction == null ? null : channelTransaction.trim();
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId == null ? null : tradeId.trim();
    }

    public String getTradeModule() {
        return tradeModule;
    }

    public void setTradeModule(String tradeModule) {
        this.tradeModule = tradeModule == null ? null : tradeModule.trim();
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getQueryTimes() {
        return queryTimes;
    }

    public void setQueryTimes(Integer queryTimes) {
        this.queryTimes = queryTimes;
    }

    public Integer getEmailStatus() {
        return emailStatus;
    }

    public void setEmailStatus(Integer emailStatus) {
        this.emailStatus = emailStatus;
    }
}