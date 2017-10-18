package com.lsh.payment.core.strategy.payVo.qfpay;


import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/25.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class QFpayResponse extends BasePayResponse {

    /**
     * 支付类型 微信扫码:800201
     */
    private String pay_type;
    /**
     * 系统时间
     */
    private String sysdtm;
    /**
     * 请求交易时间 格式为：YYYY-MM-MM HH:MM:SS
     */
    private String txdtm;
    /**
     * 错误描述
     */
    private String resperr;
    /**
     * 订单支付金额，单位分
     */
    private String txamt;
    /**
     * 调试信息
     */
    private String respmsg;
    /**
     * 外部订单号，开发者平台订单号
     */
    private String out_trade_no;
    /**
     * 钱方订单号
     */
    private String syssn;
    /**
     * 扫码时返回，二维码的url
     */
    private String qrcode;
    /**
     * 交易返回码 0000表示交易成功； 1143、1145表示交易中，需要继续查询交易结果； 其他返回码表示交易失败
     */
    private String respcd;

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

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getRespcd() {
        return respcd;
    }

    public void setRespcd(String respcd) {
        this.respcd = respcd;
    }

    public String getResperr() {
        return resperr;
    }

    public void setResperr(String resperr) {
        this.resperr = resperr;
    }

    public String getRespmsg() {
        return respmsg;
    }

    public void setRespmsg(String respmsg) {
        this.respmsg = respmsg;
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

    public String getTxdtm() {
        return txdtm;
    }

    public void setTxdtm(String txdtm) {
        this.txdtm = txdtm;
    }

}