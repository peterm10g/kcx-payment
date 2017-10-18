package com.lsh.payment.core.model.payEnum;

/**
 * Project Name: lsh-payment
 * Created by peter on 17/4/26.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public enum PayService {

    ALIPAY_SERVICE("1","aliPayServiceImpl"),
    WXPAY_SERVICE("2","wxPayServiceImpl"),
    LKLPAY_SERVICE("5","lklPayServiceImpl"),
    QFWXPAY_SERVICE("6","qfPayServiceImpl"),
    QFALIPAY_SERVICE("8","qfPayServiceImpl"),
    XYWXPAY_SERVICE("10","xyPayServiceImpl"),
    XYALIPAY_SERVICE("12","xyPayServiceImpl"),
    CMBCWXPAY_SERVICE("13","CMBCPayServiceImpl"),
    CMBCALIPAY_SERVICE("14","CMBCPayServiceImpl"),
    ALLINWXPAY_SERVICE("15","allinPayServiceImpl"),
    ALLINALIPAY_SERVICE("16","allinPayServiceImpl");

    private String code;

    private String name;

    PayService(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getServiceByCode(String code) {
        for (PayService payService : PayService.values()) {
            if (payService.getCode().equals(code)) {
                return payService.getName();
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
