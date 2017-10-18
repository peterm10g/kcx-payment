package com.lsh.payment.api.service.lklPay;

import com.lsh.payment.api.model.lklpay.LklQueryRequest;
import com.lsh.payment.api.model.lklpay.LklQueryResponse;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/26.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public interface ILklPayRestService {
//    /**
//     * 拉卡拉支付预下单接口
//     * @param paymentRequest 请求参数
//     * @return LklPayRestResponse
//     */
//    LklResponse lakalaPrePay(PaymentRequest paymentRequest);

    /**
     * 拉卡拉
     * @param lklQueryRequest 请求参数
     * @return LklPayRestResponse
     */
    LklQueryResponse lklQuery(LklQueryRequest lklQueryRequest);


}
