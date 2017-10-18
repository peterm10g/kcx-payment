package com.lsh.payment.api.model.qfpay;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/25.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QfpayRequest implements Serializable{

    private static final long serialVersionUID = -4311757322893919539L;
    private String pay_detail_id;
    private String transaction_id;
    private String money;
    private int status;

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getPay_detail_id() {
        return pay_detail_id;
    }

    public void setPay_detail_id(String pay_detail_id) {
        this.pay_detail_id = pay_detail_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }
}
