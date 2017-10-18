package com.lsh.payment.core.model.payNotifyTmsTask;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/11/8
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.payVo.payNotifyTmsTask
 * desc:
 */
@SuppressFBWarnings("EI_EXPOSE_REP")
public class PayNotifyTmsTask {
    /**
     * 主键
     */
    private Long id;
    /**
     * 业务平台交易id
     */
    private String tradeId;
    /**
     * 支付渠道 相当于 pay_channel
     */
    private Integer method;
    /**
     * 创建时间
     */
    private Date createdAt;
    /**
     * 通知次数
     */
    private Integer notifyTimes;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 修改时间
     */
    private Date updatedAt;
    /**
     * 回调url
     */
    private String lshNotifyUrl;
    /**
     * 支付金额
     */
    private BigDecimal receiveAmount;
    /**
     * 支付平台流水号
     */
    private String payPaymentNo;
    /**
     * 邮件是否发送
     */
    private Integer emailStatus;

    private String traceModule;

    public Integer getEmailStatus() {
        return emailStatus;
    }

    public void setEmailStatus(Integer emailStatus) {
        this.emailStatus = emailStatus;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getMethod() {
        return method;
    }

    public void setMethod(Integer method) {
        this.method = method;
    }

    public Integer getNotifyTimes() {
        return notifyTimes;
    }

    public void setNotifyTimes(Integer notifyTimes) {
        this.notifyTimes = notifyTimes;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLshNotifyUrl() {
        return lshNotifyUrl;
    }

    public void setLshNotifyUrl(String lshNotifyUrl) {
        this.lshNotifyUrl = lshNotifyUrl;
    }

    public BigDecimal getReceiveAmount() {
        return receiveAmount;
    }

    public void setReceiveAmount(BigDecimal receiveAmount) {
        this.receiveAmount = receiveAmount;
    }

    public String getPayPaymentNo() {
        return payPaymentNo;
    }

    public void setPayPaymentNo(String payPaymentNo) {
        this.payPaymentNo = payPaymentNo;
    }

    public String getTraceModule() {
        return traceModule;
    }

    public void setTraceModule(String traceModule) {
        this.traceModule = traceModule;
    }
}
