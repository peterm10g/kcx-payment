package com.lsh.payment.api.model.refund;

import com.lsh.payment.api.model.baseVo.BaseResponse;

/**
 * Project Name: lsh-payment
 * Created by peter on 17/7/6.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class RefundResponse extends BaseResponse {

    private String paymemt_refund_no;


    public String getPaymemt_refund_no() {
        return paymemt_refund_no;
    }

    public void setPaymemt_refund_no(String paymemt_refund_no) {
        this.paymemt_refund_no = paymemt_refund_no;
    }
}
