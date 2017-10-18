package com.lsh.payment.core.model.payEnum;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/30.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public enum ModubleType {

    BASE(1,"主模块"),VICE(2,"副模块");

    private Integer code;

    private String name;

    ModubleType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }


    public String getName() {
        return name;
    }

}
