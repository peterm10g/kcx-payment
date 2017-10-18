package com.lsh.payment.core.strategy.payVo.lklpay;

import com.lsh.payment.api.model.lklpay.LklPayContent;
import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;

/**
 * Project Name: lsh-payment
 * Created by miaozhuang on 16/10/27.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class LklPayResponse extends BasePayResponse {

    private LklPayContent content;

    public LklPayContent getContent() {
        return content;
    }

    public void setContent(LklPayContent content) {
        this.content = content;
    }
}
