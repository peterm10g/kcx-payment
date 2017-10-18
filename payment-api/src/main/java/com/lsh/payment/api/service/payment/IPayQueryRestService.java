package com.lsh.payment.api.service.payment;

import com.lsh.payment.api.model.baseVo.BaseResponse;
import com.lsh.payment.api.model.payment.PaymentQueryRequest;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/7.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public interface IPayQueryRestService {
    /**
     * 支付平台统一查询接口
     * @param paymentQueryRequest  查询参数对象
     * @return     BaseResponse    返回结果对象
     */
    BaseResponse queryPayStatus(PaymentQueryRequest paymentQueryRequest);
}
