package com.lsh.payment.core.model.payEnum;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/30.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public enum OperateStatus {
    BEGIN(0,"初始"),

    PAYMENT_NO_CALLBACK(2,"付款未回告"),

    PAYMENT_CALLBACK(22,"付款已回告");


    private int code;

    private String name;

    OperateStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }


    public String getName() {
        return name;
    }

}
