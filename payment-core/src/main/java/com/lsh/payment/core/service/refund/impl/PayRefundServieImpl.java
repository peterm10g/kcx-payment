package com.lsh.payment.core.service.refund.impl;

import com.lsh.base.common.utils.CollectionUtils;
import com.lsh.payment.core.dao.refund.PayRefundDao;
import com.lsh.payment.core.dao.refund.RefundTaskDao;
import com.lsh.payment.core.model.payEnum.RefundStatus;
import com.lsh.payment.core.model.payEnum.WxRefundStatus;
import com.lsh.payment.core.model.refund.PayRefund;
import com.lsh.payment.core.model.refund.RefundTask;
import com.lsh.payment.core.service.refund.PayRefundService;
import com.lsh.payment.core.strategy.payVo.wxpay.WxPayRefundQueryResult;
import com.lsh.payment.core.strategy.payVo.wxpay.WxPayRefundResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Project Name: lsh-payment
 * Created by peter on 17/6/28.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service
@Transactional(readOnly = true)
public class PayRefundServieImpl implements PayRefundService {

    @Autowired
    private PayRefundDao payRefundDao;

    @Autowired
    private RefundTaskDao refundTaskDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addRefund(PayRefund payRefund) {
        return payRefundDao.insertSelective(payRefund);
    }

    @Override
    public PayRefund selectRefundByPayRefundNo(String refundNo) {

        List<PayRefund> PayRefunds = payRefundDao.selectByParams(Collections.singletonMap("paymentRefundNo",(Object)refundNo));
        if (CollectionUtils.isEmpty(PayRefunds)) {
//            logger.info("channelTransaction [{}] 对应的支付记录不存在 ",channelTransaction);
            return null;
        }

        return CollectionUtils.getFirst(PayRefunds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRefundByPayRefundNo(PayRefund payRefund) {

        return payRefundDao.updateByPayRefundIdSelective(payRefund);
    }


    @Override
    public PayRefund selectRefundByRefundTradeId(String refundTradeId) {

        List<PayRefund> PayRefunds = payRefundDao.selectByParams(Collections.singletonMap("refundTradeId",(Object)refundTradeId));
        if (CollectionUtils.isEmpty(PayRefunds)) {
//            logger.info("channelTransaction [{}] 对应的支付记录不存在 ",channelTransaction);
            return null;
        }

        return CollectionUtils.getFirst(PayRefunds);
    }

    @Override
    public List<PayRefund> selectRefundByPayPaymentNo(String payPaymentNo) {
        return payRefundDao.selectByParams(Collections.singletonMap("payPaymentNo",(Object)payPaymentNo));
    }

    /**
     * @param payRefund
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRefundByWxResult2success(PayRefund payRefund,WxPayRefundResult wxPayRefundResult) {

        RefundTask refundTask = new RefundTask();
        refundTask.setRefId(payRefund.getPaymentRefundNo());
        refundTask.setCreatedAt(new Date());
        refundTask.setTimes(0);//次数
        refundTask.setUpdatedAt(new Date());
        refundTask.setIsValid(1);
        refundTask.setTaskType(1);//1-退款查询
        refundTask.setStatus(1);//1-未查询  2-退款成功
        refundTaskDao.insertSelective(refundTask);

        PayRefund payRefund4update = new PayRefund();
        payRefund4update.setPaymentRefundNo(payRefund.getPaymentRefundNo());
        payRefund4update.setChannelRefundId(wxPayRefundResult.getRefundId());
        payRefund4update.setRefundStatus(RefundStatus.REFUND_ING.getValue());

        return payRefundDao.updateByPayRefundIdSelective(payRefund4update);

    }

    /**
     * @param payRefund
     * @param wxPayRefundResult
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRefundByWxResult2fail(PayRefund payRefund, WxPayRefundResult wxPayRefundResult) {
        PayRefund payRefund4update = new PayRefund();
        payRefund4update.setPaymentRefundNo(payRefund.getPaymentRefundNo());
        payRefund4update.setChannelRefundId(wxPayRefundResult.getRefundId());
        payRefund4update.setRefundStatus(RefundStatus.REFUND_EXCEPTION.getValue());

        return payRefundDao.updateByPayRefundIdSelective(payRefund4update);
    }

    /**
     * @param payRefund
     * @param wxPayRefundQueryResult
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRefundFromWxQueryResult(PayRefund payRefund, WxPayRefundQueryResult wxPayRefundQueryResult) {
        for(WxPayRefundQueryResult.RefundRecord refundRecord : wxPayRefundQueryResult.getRefundRecords()){
            if(refundRecord.getRefundStatus().equals(WxRefundStatus.WX_REFUND_SUCCESS.getName())){
                PayRefund payRefund4update = new PayRefund();
                payRefund4update.setPaymentRefundNo(payRefund.getPaymentRefundNo());
                payRefund4update.setRefundRecvAccout(refundRecord.getRefundRecvAccout());
                payRefund4update.setRefundStatus(RefundStatus.REFUND_SUCCESS.getValue());
                payRefundDao.updateByPayRefundIdSelective(payRefund4update);
                //修改退款任务状态
                RefundTask refundTask = new RefundTask();
                refundTask.setRefId(payRefund.getPaymentRefundNo());
                refundTask.setStatus(2);

                return refundTaskDao.updateByRefId(refundTask);
            }
        }

        return 0;
    }
}
