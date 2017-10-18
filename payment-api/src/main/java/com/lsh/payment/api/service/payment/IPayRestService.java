package com.lsh.payment.api.service.payment;

import com.lsh.payment.api.model.baseVo.BaseResponse;
import com.lsh.payment.api.model.payment.PaymentRequest;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/8.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public interface IPayRestService {
    /**
     * 支付平台统一下单接口
     * @param paymentRequest   下单对象
     * @return                 BaseResponse 返回值对象
     */
    BaseResponse prePayment(PaymentRequest paymentRequest);
}
