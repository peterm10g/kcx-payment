package com.lsh.payment.api.model.payment;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/8.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentRequest implements Serializable{

    @NotBlank
    private String trade_id;

    @NotBlank
    private String pay_way;

    @NotBlank
    private String channel_type;

    @NotNull
    @Digits(integer = 8,fraction = 2,message = "支付金额不合法,小数点后最多两位,且大于0,例如(12.25,0.88)")
    private String request_amount;

    @NotBlank
    private String notify_url;

    private String trade_module;

    private String return_url;

    private String openid;

    private Integer system;

    private String ext;

    public String getTrade_id() {
        return trade_id;
    }

    public void setTrade_id(String trade_id) {
        this.trade_id = trade_id;
    }

    public String getPay_way() {
        return pay_way;
    }

    public void setPay_way(String pay_way) {
        this.pay_way = pay_way;
    }

    public String getChannel_type() {
        return channel_type;
    }

    public void setChannel_type(String channel_type) {
        this.channel_type = channel_type;
    }

    public String getRequest_amount() {
        return request_amount;
    }

    public void setRequest_amount(String request_amount) {
        this.request_amount = request_amount;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getReturn_url() {
        return return_url;
    }

    public void setReturn_url(String return_url) {
        this.return_url = return_url;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getTrade_module() {
        return trade_module;
    }

    public void setTrade_module(String trade_module) {
        this.trade_module = trade_module;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public Integer getSystem() {
        return system;
    }

    public void setSystem(Integer system) {
        this.system = system;
    }
}
