package com.lsh.payment.api.model.baseVo;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/24.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class BaseRequest implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = -8338882831901473766L;

    @NotBlank
    private String trade_id;

    @NotBlank
    private String pay_way;

    @NotBlank
    private String channel_type;

    @NotNull
    private BigDecimal request_amount;

    @NotBlank
    private String notify_url;

    @NotBlank
    private String trade_module;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

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

    public BigDecimal getRequest_amount() {
        return request_amount;
    }

    public void setRequest_amount(BigDecimal request_amount) {
        this.request_amount = request_amount;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getTrade_module() {
        return trade_module;
    }

    public void setTrade_module(String trade_module) {
        this.trade_module = trade_module;
    }
}
