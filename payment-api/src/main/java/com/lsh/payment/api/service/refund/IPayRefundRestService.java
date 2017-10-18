package com.lsh.payment.api.service.refund;

import com.lsh.payment.api.model.baseVo.BaseResponse;
import com.lsh.payment.api.model.refund.RefundQueryRequest;
import com.lsh.payment.api.model.refund.RefundRequest;

/**
 * Project Name: lsh-payment
 * Created by peter on 17/6/28.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public interface IPayRefundRestService {

    /**
     * 微信 支付宝  钱方 统一退款申请接口
     * @param refundRequest 下单参数
     * @return BaseResponse
     */
    BaseResponse refund(RefundRequest refundRequest);


    /**
     * 退款查询接口
     * @param refundQueryRequest 下单参数
     * @return BaseResponse
     */
    BaseResponse refundQuery(RefundQueryRequest refundQueryRequest);
}
