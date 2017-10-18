package com.lsh.payment.core.service.payment;

import com.lsh.payment.api.model.baseVo.QueryContent;
import com.lsh.payment.core.model.payment.PayDeal;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/26.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public interface IPayQueryChannelService {

    /**
     * 统一查询接口
     * @param payDeal 查询支付记录对象
     * @return        查询结果对象
     */
    QueryContent query(PayDeal payDeal);
}
