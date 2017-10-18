package com.lsh.payment.api.model.wxpay;

import com.lsh.payment.api.model.baseVo.BaseContent;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/12/1.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class WxH5Content extends BaseContent {

    private String title;
    @JsonProperty("return_url")
    private String returnUrl;
    private String openid;

    private WxH5Result result;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public WxH5Result getResult() {
        return result;
    }

    public void setResult(WxH5Result result) {
        this.result = result;
    }
}
