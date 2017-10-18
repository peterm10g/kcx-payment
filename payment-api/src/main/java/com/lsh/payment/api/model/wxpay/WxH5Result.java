package com.lsh.payment.api.model.wxpay;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/12/1.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class WxH5Result implements Serializable {
    private String appId;
    private String nonceStr;
    @JsonProperty("package")
    private String wxH5Package;
    private String signType;
    private String timeStamp;
    private String paySign;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getWxH5Package() {
        return wxH5Package;
    }

    public void setWxH5Package(String wxH5Package) {
        this.wxH5Package = wxH5Package;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getPaySign() {
        return paySign;
    }

    public void setPaySign(String paySign) {
        this.paySign = paySign;
    }
}
