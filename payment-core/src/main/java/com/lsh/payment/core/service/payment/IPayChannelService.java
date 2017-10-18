package com.lsh.payment.core.service.payment;

import com.lsh.payment.api.model.baseVo.BaseResponse;
import com.lsh.payment.api.model.payment.PaymentRequest;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/25.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public interface IPayChannelService {

    /**
     * 微信 支付宝  钱方 统一下单接口
     * @param paymentRequest 下单参数
     * @return BaseResponse
     */
    BaseResponse prepay(PaymentRequest paymentRequest);

}
