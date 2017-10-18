package com.lsh.payment.core.service.refund;

import com.lsh.payment.core.model.refund.PayRefund;
import com.lsh.payment.core.strategy.payVo.wxpay.WxPayRefundQueryResult;
import com.lsh.payment.core.strategy.payVo.wxpay.WxPayRefundResult;

import java.util.List;

/**
 * Project Name: lsh-payment
 * Created by peter on 17/6/28.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public interface PayRefundService {

    /**
     *
     * @param payRefund
     * @return
     */
    int addRefund(PayRefund payRefund);

    /**
     *
     * @param refundNo
     * @return
     */
    PayRefund selectRefundByPayRefundNo(String refundNo);

    /**
     *
     * @param payRefund
     * @return
     */
    int updateRefundByPayRefundNo(PayRefund payRefund);

    /**
     *
     * @param refundTradeId
     * @return
     */
    PayRefund selectRefundByRefundTradeId(String refundTradeId);

    /**
     *
     * @param payPaymentNo
     * @return
     */
    List<PayRefund> selectRefundByPayPaymentNo(String payPaymentNo);

    /**
     *
     * @param payRefund
     * @return
     */
    int updateRefundByWxResult2success(PayRefund payRefund,WxPayRefundResult wxPayRefundResult);

    /**
     *
     * @param payRefund
     * @return
     */
    int updateRefundByWxResult2fail(PayRefund payRefund,WxPayRefundResult wxPayRefundResult);
    /**
     *
     * @param payRefund
     * @return
     */
    int updateRefundFromWxQueryResult(PayRefund payRefund,WxPayRefundQueryResult wxPayRefundQueryResult);


}
