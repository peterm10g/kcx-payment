package com.lsh.payment.core.service.payment.impl;


import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.api.model.payment.PaymentRequest;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.Async.CallThirdPayFailModel;
import com.lsh.payment.core.model.Async.ResultNotifyModel;
import com.lsh.payment.core.model.PayTask.PayTask;
import com.lsh.payment.core.model.payEnum.PayChannel;
import com.lsh.payment.core.model.payEnum.PayStatus;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.service.AsyncService.AsyncEvent;
import com.lsh.payment.core.service.payTask.PayTaskService;
import com.lsh.payment.core.service.payment.PayFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/10/31
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.service.payment
 * desc:
 */
@Service
@Transactional(readOnly = true)
public class PayBaseService {

    @Autowired
    private PayDealService payDealService;

    @Autowired
    private PayTaskService payTaskService;

    private static Logger logger = LoggerFactory.getLogger(PayBaseService.class);

    /**
     * 预下单入库
     *
     * @param paymentRequest 申请授权参数
     * @return 支付记录
     */
    @Transactional(rollbackFor = Exception.class)
    public PayDeal prePayment(PaymentRequest paymentRequest) {
        PayDeal payDeal = PayFactory.create(paymentRequest);

        PayDeal dbPayDeal = this.dealPaymentNo(payDeal);

        if (dbPayDeal != null) {
            return dbPayDeal;
        }

        return payDeal;
    }

    /**
     * 关闭未支付订单
     * @param payDeal 支付记录
     * @return        PayDeal
     */
    private PayDeal dealPaymentNo(PayDeal payDeal) {
        Map<String, Object> params = new HashMap<>();
        params.put("tradeId", payDeal.getTradeId());
        params.put("tradeModule", payDeal.getTradeModule());

        List<PayDeal> payDeals = payDealService.queryPayDeals(params);

        PayDeal pd;
        for (PayDeal pl : payDeals) {
            pd = new PayDeal();
            pd.setPayId(pl.getPayId());

            if (pl.getPayStatus() == PayStatus.PAY_SUCCESS.getValue()) {
                if (PayChannel.QFPAY.getName().equals(pl.getPayChannel())) {
                    //钱方,特殊处理
                    return pl;
                }
                throw new BusinessException(ExceptionStatus.E2001003.getCode(), ExceptionStatus.E2001003.getMessage());
            }

            if (pl.getPayStatus() == PayStatus.CREATE_PAYMENT.getValue()
                    || pl.getPayStatus() == PayStatus.PAYING.getValue()) {
                pd.setPayStatus(PayStatus.PAY_CLOSE.getValue());
                pd.setDoneTime(new Date());
                payDealService.updatePayDeal(pd);
//                if (payDealService.updatePayDeal(pd) > 0) {
////                    payTaskService.deletePayTask(pd.getPayId());
//                }
            }
        }

        if (payDealService.addPayDeal(payDeal) <= 0) {

            throw new BusinessException(ExceptionStatus.E2001001.getCode(), ExceptionStatus.E2001001.getMessage());
        }

        return null;
    }

    /**
     * 添加支付扫描任务,用于处理未收到回调信息支付
     *
     * @param payDeal 支付记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void addPayTask(PayDeal payDeal) {

        try {

            PayTask payTask = new PayTask();
            payTask.setTradeId(payDeal.getTradeId());
            payTask.setPayId(payDeal.getPayId());
            payTask.setPayPaymentNo(payDeal.getPayPaymentNo());
            payTask.setChannelTransaction(payDeal.getChannelTransaction());
            payTask.setTradeModule(payDeal.getTradeModule());
            payTask.setCreatedAt(new Date());
            payTask.setUpdatedAt(new Date());

            payTaskService.addPayTask(payTask);
        } catch (Exception ex) {
            logger.error(" add payTask error ", ex);
        }
    }

    /**
     * 更新支付为成功或失败,并删除task
     *
     * @param payDeal 支付记录
     */
    @Transactional(rollbackFor = Exception.class)
    public void updPayDeal(PayDeal payDeal, PayDeal payDealInfo) {
        //更新支付为成功或失败,并删除task
        payDeal.setDoneTime(new Date());
        if (payDealService.updatePayDeal(payDeal) > 0) {
            payTaskService.deletePayTask(payDeal.getPayId());
            if (PayStatus.PAY_SUCCESS.getValue() == payDeal.getPayStatus()) {
                //成功,通知TMS
                logger.info("{}开始通知业务方",payDeal.getPayId());
                AsyncEvent.post(new ResultNotifyModel(payDealInfo));
            }
        } else {
            logger.error("更新支付流水失败:" + payDeal.getPayId());
            throw new BusinessException(ExceptionStatus.E2003001.getCode(), ExceptionStatus.E2003001.getMessage());
        }
    }

    /**
     * 支付失败监控
     * @param flag     标记
     * @param errorMsg 失败标记
     * @param req      返回对象
     */
    public static void monitor(String flag, String errorMsg, Object req) {
        try {
            AsyncEvent.post(new CallThirdPayFailModel(flag, errorMsg, req));
        } catch (Throwable e) {
            logger.error("http 预警失败", e);
        }
    }


}
