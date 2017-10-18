package com.lsh.payment.core.model.payLog;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Date;

@SuppressFBWarnings({"EI_EXPOSE_REP2", "EI_EXPOSE_REP"})
public class PayLog {
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
     * 业务平台订单号
     */
    private String tradeId;
    /**
     * 交易类型
     */
    private Integer payType;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 入参
     */
    private String params;
    /**
     * 返回结果
     */
    private String backresult;
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

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId == null ? null : tradeId.trim();
    }

    public Integer getPayType() {
		return payType;
	}

	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params == null ? null : params.trim();
    }

    public String getBackresult() {
        return backresult;
    }

    public void setBackresult(String backresult) {
        this.backresult = backresult == null ? null : backresult.trim();
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
}