package com.lsh.payment.core.model.refund;

import java.math.BigDecimal;
import java.util.Date;

public class PayRefund {
    private Long id;

    private String paymentRefundNo;

    private String refundTradeId;

    private String payPaymentNo;

    private String channelRefundId;

    private BigDecimal refundAmount;

    private String refundChannel;

    private String amountType;

    private String refundRecvAccout;

    private Date createAt;

    private Date updateAt;

    private Date refundAt;

    private Integer isValid;

    private Integer refundStatus;

    private Integer callSystem;

    private String ext;

    private String refundNotifyUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentRefundNo() {
        return paymentRefundNo;
    }

    public void setPaymentRefundNo(String paymentRefundNo) {
        this.paymentRefundNo = paymentRefundNo == null ? null : paymentRefundNo.trim();
    }

    public String getPayPaymentNo() {
        return payPaymentNo;
    }

    public void setPayPaymentNo(String payPaymentNo) {
        this.payPaymentNo = payPaymentNo == null ? null : payPaymentNo.trim();
    }

    public String getRefundTradeId() {
        return refundTradeId;
    }

    public void setRefundTradeId(String refundTradeId) {
        this.refundTradeId = refundTradeId;
    }

    public String getChannelRefundId() {
        return channelRefundId;
    }

    public void setChannelRefundId(String channelRefundId) {
        this.channelRefundId = channelRefundId == null ? null : channelRefundId.trim();
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getRefundChannel() {
        return refundChannel;
    }

    public void setRefundChannel(String refundChannel) {
        this.refundChannel = refundChannel == null ? null : refundChannel.trim();
    }

    public String getAmountType() {
        return amountType;
    }

    public void setAmountType(String amountType) {
        this.amountType = amountType == null ? null : amountType.trim();
    }

    public String getRefundRecvAccout() {
        return refundRecvAccout;
    }

    public void setRefundRecvAccout(String refundRecvAccout) {
        this.refundRecvAccout = refundRecvAccout == null ? null : refundRecvAccout.trim();
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public Date getRefundAt() {
        return refundAt;
    }

    public void setRefundAt(Date refundAt) {
        this.refundAt = refundAt;
    }

    public Integer getIsValid() {
        return isValid;
    }

    public void setIsValid(Integer isValid) {
        this.isValid = isValid;
    }

    public Integer getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(Integer refundStatus) {
        this.refundStatus = refundStatus;
    }

    public Integer getCallSystem() {
        return callSystem;
    }

    public void setCallSystem(Integer callSystem) {
        this.callSystem = callSystem;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext == null ? null : ext.trim();
    }

    public String getRefundNotifyUrl() {
        return refundNotifyUrl;
    }

    public void setRefundNotifyUrl(String refundNotifyUrl) {
        this.refundNotifyUrl = refundNotifyUrl == null ? null : refundNotifyUrl.trim();
    }
}