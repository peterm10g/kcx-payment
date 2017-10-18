package com.lsh.payment.api.model.baseVo;

import java.io.Serializable;

/**
 * Project Name: lsh-payment
 * Created by huangdong on 16/7/7.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class CommonResult<T> implements Serializable {

    private static final long serialVersionUID = -7828671964068316006L;

    private String code;

    private String message;

    private T data;

    public CommonResult() {
    }

    public CommonResult(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public CommonResult(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

