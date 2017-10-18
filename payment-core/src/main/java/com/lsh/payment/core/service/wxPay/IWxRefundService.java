package com.lsh.payment.core.service.wxPay;

import com.lsh.payment.core.strategy.payVo.wxpay.WxPayRefundQueryResult;
import com.lsh.payment.core.strategy.payVo.wxpay.WxPayRefundResult;

import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 17/6/29.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public interface IWxRefundService {


    WxPayRefundResult refund(Map<String, String> dataMap);



    WxPayRefundQueryResult refundQuery(Map<String, String> dataMap);
}
