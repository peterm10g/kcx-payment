package com.lsh.payment.core.model.Async;

import com.lsh.payment.core.model.payment.PayDeal;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 17/1/11
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.model.Async
 * desc:插入payTask表异步处理
 */
public class payTaskModel {
    private PayDeal payDeal;

    public payTaskModel(PayDeal payDeal) {
        this.payDeal = payDeal;
    }

    public PayDeal getPayDeal() {
        return payDeal;
    }

    public void setPayDeal(PayDeal payDeal) {
        this.payDeal = payDeal;
    }
}
