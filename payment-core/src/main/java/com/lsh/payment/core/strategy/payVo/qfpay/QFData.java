package com.lsh.payment.core.strategy.payVo.qfpay;

import java.io.Serializable;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/10/27
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.payChannel.qfpay
 * desc:钱方查询返回的订单信息
 */
public class QFData implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 76575214382345747L;

    //    pay_type 支付类型 微信刷卡:800208
    private String pay_type;

    //    sysdtm 系统交易时间
    private String sysdtm;

    // order_type     订单类型 支付的订单：payment；退款的订单：refund；关闭的订单：close
    private String order_type;

    // txcurrcd 币种 港币：HKD ；人民币：CNY
    private String txcurrcd;

    // txdtm 请求交易时间 格式为：YYYY-MM-MM HH:MM:SS
    private String txdtm;

    // txamt 订单支付金额，单位分
    private String txamt;

    // out_trade_no 外部订单号，开发者平台订单号
    private String out_trade_no;

    // syssn 钱方订单号
    private String syssn;

    // cancel 撤销/退款标记 正常交易：0；已撤销：2；已退货：3
    private String cancel;

    // respcd 交易结果码
    private String respcd;

    // errmsg
    private String errmsg;

    public String getCancel() {
        return cancel;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getRespcd() {
        return respcd;
    }

    public void setRespcd(String respcd) {
        this.respcd = respcd;
    }

    public String getSysdtm() {
        return sysdtm;
    }

    public void setSysdtm(String sysdtm) {
        this.sysdtm = sysdtm;
    }

    public String getSyssn() {
        return syssn;
    }

    public void setSyssn(String syssn) {
        this.syssn = syssn;
    }

    public String getTxamt() {
        return txamt;
    }

    public void setTxamt(String txamt) {
        this.txamt = txamt;
    }

    public String getTxcurrcd() {
        return txcurrcd;
    }

    public void setTxcurrcd(String txcurrcd) {
        this.txcurrcd = txcurrcd;
    }

    public String getTxdtm() {
        return txdtm;
    }

    public void setTxdtm(String txdtm) {
        this.txdtm = txdtm;
    }
}
