package com.lsh.payment.core.model.Async;

import com.lsh.payment.core.model.payment.PayDeal;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/12/22
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.model.Async
 * desc:
 */
public class ResultNotifyModel {

    private PayDeal payDeal;

    public ResultNotifyModel(PayDeal payDeal) {
        this.payDeal = payDeal;
    }

    public PayDeal getPayDeal() {
        return payDeal;
    }

    public void setPayDeal(PayDeal payDeal) {
        this.payDeal = payDeal;
    }
}
