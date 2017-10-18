package com.lsh.payment.core.handler;

import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.api.model.refund.RefundResponse;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.model.refund.PayRefund;
import com.lsh.payment.core.service.refund.PayRefundService;
import com.lsh.payment.core.service.wxPay.IWxRefundService;
import com.lsh.payment.core.strategy.payVo.wxpay.WxPayRefundResult;
import com.lsh.payment.core.util.BigDecimalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 17/7/6.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service
public class WxRefundHandler {

    private static Logger logger = LoggerFactory.getLogger(WxRefundHandler.class);

    @Autowired
    private PayRefundService payRefundService;
    @Autowired
    private IWxRefundService wxRefundService;

    public RefundResponse refund(PayDeal payDeal, PayRefund payRefund){

        RefundResponse refundResponse = new RefundResponse();
        //申请退款
        Map<String, String> wxRefundRequestBody = new HashMap<>(10);
        wxRefundRequestBody.put("transaction_id", payDeal.getChannelTransaction());//微信支付流水号
        wxRefundRequestBody.put("out_refund_no", payRefund.getPaymentRefundNo());//支付平台退款流水号
        wxRefundRequestBody.put("total_fee", BigDecimalUtils.yuanToFen(payDeal.getReceiveAmount()).toString());
        wxRefundRequestBody.put("refund_fee", BigDecimalUtils.yuanToFen(payRefund.getRefundAmount()).toString());
        WxPayRefundResult wxPayRefundResult = wxRefundService.refund(wxRefundRequestBody);

        if (wxPayRefundResult.getReturnCode().equals("SUCCESS")) {

            if(wxPayRefundResult.getResultCode().equals("SUCCESS")){

                if(payRefundService.updateRefundByWxResult2success(payRefund, wxPayRefundResult) > 0){
                    refundResponse.setPaymemt_refund_no(wxPayRefundResult.getOutRefundNo());
                }

                refundResponse.setRet(Integer.parseInt(ExceptionStatus.SUCCESS.getCode()));
                refundResponse.setMsg(ExceptionStatus.SUCCESS.getMessage());
            }else{

                if(payRefundService.updateRefundByWxResult2fail(payRefund, wxPayRefundResult) > 0){
                    refundResponse.setPaymemt_refund_no(wxPayRefundResult.getOutRefundNo());
                }

                logger.info("wxPayRefundResult info is " + JSON.toJSONString(wxPayRefundResult));
                refundResponse.setRet(2001005);
                refundResponse.setMsg("退款申请失败," + wxPayRefundResult.getErrCode() + "," + wxPayRefundResult.getErrCodeDes());
                refundResponse.setPaymemt_refund_no(payRefund.getPaymentRefundNo());
            }

        }else{
//            TODO
        }

        return refundResponse;
    }


}
