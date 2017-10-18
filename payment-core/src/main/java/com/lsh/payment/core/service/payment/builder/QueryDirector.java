package com.lsh.payment.core.service.payment.builder;

import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/27.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class QueryDirector {

    private QueryBuilder queryBuilder;

    public QueryDirector(QueryBuilder queryBuilder) {

        this.queryBuilder = queryBuilder;
    }

    public void queryParse(BasePayResponse basePayResponse, PayDeal payDeal){

        queryBuilder.parse(basePayResponse, payDeal);

        queryBuilder.createUpdatePaydeal(basePayResponse,payDeal);
    }


}
