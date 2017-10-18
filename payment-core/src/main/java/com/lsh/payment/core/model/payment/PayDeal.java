package com.lsh.payment.core.model.payment;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.math.BigDecimal;
import java.util.Date;

@SuppressFBWarnings({"EI_EXPOSE_REP2", "EI_EXPOSE_REP"})
public class PayDeal {
    /**
     * 主键
     */
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
     * 第三方平台流水号
     */
    private String channelTransaction;
    /**
     * 交易平台订单号
     */
    private String tradeId;
    /**
     * 交易类型
     */
    private String tradeModule;
    /**
     * 支付方式
     */
    private String payWay;
    /**
     * 支付类型
     */
    private Integer payType;
    /**
     * 支付状态
     */
    private Integer payStatus;
    /**
     * 操作类型
     */
    private Integer operateStatus;
    /**
     * 请求金额
     */
    private BigDecimal requestAmount;
    /**
     * 支付金额
     */
    private BigDecimal receiveAmount;
    /**
     * 支付渠道
     */
    private String payChannel;
    /**
     * 支付模块
     */
    private Integer moduleType;
    /**
     * 调用系统
     */
    private Integer system;
    /**
     * 描述信息
     */
    private String ext;
    /**
     * 支付时间
     */
    private Date payTime;
    /**
     * 查询时间
     */
    private Date queryTime;
    /**
     * 完成时间
     */
    private Date doneTime;
    /**
     * 创建时间
     */
    private Date createdAt;
    /**
     * 修改时间
     */
    private Date updatedAt;
    /**
     * 操作时间
     */
    private Integer operateTime;
    /**
     * 回调url
     */
    private String lshNotifyUrl;

    /**
     *支付具体类型 1 支付宝app ， 2微信app，3 支付宝H5，4 微信H5，5 拉卡拉app，6微信扫码 ，8支付宝扫码
     */
    private Integer tradeType;

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

    public String getPayWay() {
        return payWay;
    }

    public void setPayWay(String payWay) {
        this.payWay = payWay == null ? null : payWay.trim();
    }

    public BigDecimal getRequestAmount() {
        return requestAmount;
    }

    public void setRequestAmount(BigDecimal requestAmount) {
        this.requestAmount = requestAmount;
    }

    public BigDecimal getReceiveAmount() {
        return receiveAmount;
    }

    public void setReceiveAmount(BigDecimal receiveAmount) {
        this.receiveAmount = receiveAmount;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel == null ? null : payChannel.trim();
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext == null ? null : ext.trim();
    }


    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }


    public Date getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(Date queryTime) {
        this.queryTime = queryTime;
    }

    public Date getDoneTime() {
        return doneTime;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public void setDoneTime(Date doneTime) {
        this.doneTime = doneTime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Integer operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public Integer getOperateStatus() {
        return operateStatus;
    }

    public void setOperateStatus(Integer operateStatus) {
        this.operateStatus = operateStatus;
    }

    public Integer getModuleType() {
        return moduleType;
    }

    public void setModuleType(Integer moduleType) {
        this.moduleType = moduleType;
    }

    public Integer getSystem() {
        return system;
    }

    public void setSystem(Integer system) {
        this.system = system;
    }

    public String getLshNotifyUrl() {
        return lshNotifyUrl;
    }

    public void setLshNotifyUrl(String lshNotifyUrl) {
        this.lshNotifyUrl = lshNotifyUrl;
    }

    public Integer getTradeType() {
        return tradeType;
    }

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }
}