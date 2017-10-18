package com.lsh.payment.api.model.lklpay;

import java.io.Serializable;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/30.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class LklPayNotifyResponse implements Serializable{

    private String return_code;

    private String return_msg;

    public LklPayNotifyResponse() {
    }

    public LklPayNotifyResponse(String return_msg, String return_code) {
        this.return_msg = return_msg;
        this.return_code = return_code;
    }

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }
}
