package com.lsh.payment.core.strategy.payVo.alipay;

import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/25.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class AliPrePayResponse extends BasePayResponse {

    private  String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
