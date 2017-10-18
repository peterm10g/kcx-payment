package com.lsh.payment.api.model.wxpay;

import com.lsh.payment.api.model.baseVo.BaseContent;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/12/1.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class WxAppContent extends BaseContent {

    private WxAppResult result;

    public WxAppResult getResult() {
        return result;
    }

    public void setResult(WxAppResult result) {
        this.result = result;
    }
}
