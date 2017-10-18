package com.lsh.payment.api.model.lklpay;

import com.lsh.payment.api.model.baseVo.BaseResponse;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/26.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class LklResponse<T> extends BaseResponse {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 3175869214382345747L;

    private T content;

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
