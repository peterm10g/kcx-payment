package com.lsh.payment.api.model.wxpay;

import com.lsh.payment.api.model.baseVo.BaseResponse;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/12/1.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class WxResponse<T> extends BaseResponse {

    private T content;

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
