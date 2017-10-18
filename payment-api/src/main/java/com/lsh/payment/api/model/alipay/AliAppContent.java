package com.lsh.payment.api.model.alipay;

import com.lsh.payment.api.model.baseVo.BaseContent;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/12/1.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class AliAppContent extends BaseContent {

//    private boolean result;

    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

//    @JsonProperty("order_info")
//    private String orderInfo;

//    public boolean isResult() {
//        return result;
//    }
//
//    public void setResult(boolean result) {
//        this.result = result;
//    }

//    public String getOrderInfo() {
//        return orderInfo;
//    }
//
//    public void setOrderInfo(String orderInfo) {
//        this.orderInfo = orderInfo;
//    }
}
