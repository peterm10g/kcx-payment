package com.lsh.payment.worker.service;

import com.lsh.payment.core.model.payEnum.IsValid;
import com.lsh.payment.core.model.payEnum.RefundStatus;
import com.lsh.payment.core.model.payEnum.WxRefundStatus;
import com.lsh.payment.core.model.refund.PayRefund;
import com.lsh.payment.core.model.refund.RefundTask;
import com.lsh.payment.core.service.refund.IRefundTaskService;
import com.lsh.payment.core.service.refund.PayRefundService;
import com.lsh.payment.core.service.wxPay.IWxRefundService;
import com.lsh.payment.core.strategy.payVo.wxpay.WxPayRefundQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/9.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service
public class RefundTaskJopService {

    private static Logger logger = LoggerFactory.getLogger(RefundTaskJopService.class);

    @Autowired
    private IRefundTaskService refundTaskService;

    @Autowired
    private IWxRefundService wxRefundService;

    @Autowired
    private PayRefundService payRefundService;


    /**
     *
     */
    public List<RefundTask> getRefundTasks(){

        RefundTask refundTask = new RefundTask();
        refundTask.setStatus(RefundStatus.REFUND_NEW.getValue());
        refundTask.setIsValid(IsValid.VALID_TRUE.getValue());

        return refundTaskService.selectRefundTask4Task(refundTask);
    }

    public void dealRefundTask(RefundTask refundTask){
        Map<String, String> wxRefundQueryRequestBody = new HashMap<>(10);

        wxRefundQueryRequestBody.put("paymentRefundNo", refundTask.getRefId());//支付平台退款流水号
        WxPayRefundQueryResult wxPayRefundQueryResult = wxRefundService.refundQuery(wxRefundQueryRequestBody);
        if(wxPayRefundQueryResult.getResultCode().equals(WxRefundStatus.WX_REFUND_SUCCESS.getName()) && wxPayRefundQueryResult.getReturnCode().equals(WxRefundStatus.WX_REFUND_SUCCESS.getName())){
            for(WxPayRefundQueryResult.RefundRecord refundRecord : wxPayRefundQueryResult.getRefundRecords()){

                logger.info("refundRecord is " + refundRecord);
                if(refundRecord.getRefundStatus().equals(WxRefundStatus.WX_REFUND_SUCCESS.getName())){
                    PayRefund payRefund4update = new PayRefund();
                    payRefund4update.setPaymentRefundNo(refundTask.getRefId());
                    payRefund4update.setRefundRecvAccout(refundRecord.getRefundRecvAccout());
                    payRefund4update.setRefundStatus(RefundStatus.REFUND_SUCCESS.getValue());
                    payRefundService.updateRefundByPayRefundNo(payRefund4update);

                    refundTaskService.updateRefundTask(refundTask.getId(),RefundStatus.REFUND_ING.getValue());
                }
            }
        }
    }


}
