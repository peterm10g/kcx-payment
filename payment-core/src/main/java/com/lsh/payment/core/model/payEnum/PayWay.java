package com.lsh.payment.core.model.payEnum;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/26.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public enum PayWay {

    IOS("ios",1),ANDROID("android",2),H5("h5",3);

    private String name;

    private int code;

    PayWay(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }


    public int getCode() {
        return code;
    }


}
