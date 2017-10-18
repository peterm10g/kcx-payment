package com.lsh.payment.api.model.qfpay;

import com.lsh.payment.api.model.baseVo.BaseResponse;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/10/31
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.api.payVo.qfpay
 * desc:
 */
public class QfResponse<T> extends BaseResponse {
    private T content;

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
