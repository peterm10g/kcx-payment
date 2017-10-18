package com.lsh.payment.core.strategy.payVo.qfpay;

import java.io.Serializable;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/12.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class QfNotifyInfo implements Serializable {

    //交易状态码1：支付成功
    private String status;
    //sysdtm 系统交易时间
    private String sysdtm;
    private String goods_name;
    //CNY
    private String txcurrcd;
    //撤销/退款标记 正常交易：0；已撤销：2；已退货：3
    private String cancel;
    //支付类型 支付宝扫码:800101； 支付宝反扫:80010；微信扫码:800201； 微信刷卡:800208； 微信公众号支付:800207
    private String pay_type;
    //请求交易时间
    private String txdtm;
    //订单支付金额，单位分
    private String txamt;
    //out_trade_no
    private String out_trade_no;
    //交易流水号
    private String syssn;
    //"0000" 交易返回码，默认返回所有返回码状态的订单
    private String respcd;
    //通知类型payment：支付； refund：退款； close：关闭
    private String notify_type;


//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }

    public String getSysdtm() {
        return sysdtm;
    }

    public void setSysdtm(String sysdtm) {
        this.sysdtm = sysdtm;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getTxcurrcd() {
        return txcurrcd;
    }

    public void setTxcurrcd(String txcurrcd) {
        this.txcurrcd = txcurrcd;
    }

    public String getCancel() {
        return cancel;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public String getTxdtm() {
        return txdtm;
    }

    public void setTxdtm(String txdtm) {
        this.txdtm = txdtm;
    }

    public String getTxamt() {
        return txamt;
    }

    public void setTxamt(String txamt) {
        this.txamt = txamt;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getSyssn() {
        return syssn;
    }

    public void setSyssn(String syssn) {
        this.syssn = syssn;
    }

    public String getRespcd() {
        return respcd;
    }

    public void setRespcd(String respcd) {
        this.respcd = respcd;
    }

    public String getNotify_type() {
        return notify_type;
    }

    public void setNotify_type(String notify_type) {
        this.notify_type = notify_type;
    }
}
