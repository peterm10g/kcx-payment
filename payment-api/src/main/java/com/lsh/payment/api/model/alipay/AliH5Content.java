package com.lsh.payment.api.model.alipay;

import com.lsh.payment.api.model.baseVo.BaseContent;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/12/1.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class AliH5Content extends BaseContent {

    private String title;
    @JsonProperty("return_url")
    private String returnUrl;

    private String result;

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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
