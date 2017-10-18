package com.lsh.payment.core.strategy.payVo.baseVo;

import java.io.Serializable;


public class BasePayResponse implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 47575214382345747L;
    /**
     * 1-成功,2-失败,其他:第三方返回的错误信息
     */
    private  String code;

    private  String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
