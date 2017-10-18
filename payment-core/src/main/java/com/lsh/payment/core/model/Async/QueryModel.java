package com.lsh.payment.core.model.Async;

import com.lsh.payment.core.model.payment.PayDeal;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 17/1/11
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.model.Async
 * desc:统一查询接口。查询钱方异步处理
 */
public class QueryModel {
    private PayDeal payDeal;

    public QueryModel(PayDeal payDeal) {
        this.payDeal = payDeal;
    }

    public PayDeal getPayDeal() {
        return payDeal;
    }

    public void setPayDeal(PayDeal payDeal) {
        this.payDeal = payDeal;
    }
}
