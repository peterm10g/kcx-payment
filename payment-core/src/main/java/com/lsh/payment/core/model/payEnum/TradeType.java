package com.lsh.payment.core.model.payEnum;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/26.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public enum TradeType {

    ALIAPP(1,"AliApp"),
    WXAPP(2,"WxApp"),
    ALIH5(3,"AliH5"),
    WXH5(4,"WxH5"),
    LKL(5,"Lkl"),
    QFWXSM(6,"WxSm"),
    QFALISM(8,"AliSm"),
    XYWXSM(10,"WxSm"),
    XYALISM(12,"AliSm"),
    CMBCWXSM(13,"WxSm"),
    CMBCALISM(14,"AliSm"),
    ALLINWXSM(15,"WxSm"),
    ALLINALISM(16,"AliSm");

    private int code;

    private String name;

    TradeType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getTradeTypeByCode(int code) {
        for (TradeType tradeType : TradeType.values()) {
            if (tradeType.getCode() == code) {
                return tradeType.getName();
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
    
}
