package com.lsh.payment.api.model.qfpay;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/10/31
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.api.payVo.qfpay
 * desc:
 */
public class QfQueryContent {
    private String status;
    private String trade_id;
    private String transaction_id;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTrade_id() {
        return trade_id;
    }

    public void setTrade_id(String trade_id) {
        this.trade_id = trade_id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }
}
