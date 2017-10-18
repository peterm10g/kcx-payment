package com.lsh.payment.worker.service;

import com.lsh.payment.api.model.baseVo.QueryContent;
import com.lsh.payment.api.model.payment.PaymentQueryRequest;
import com.lsh.payment.core.model.PayTask.PayTask;
import com.lsh.payment.core.model.payEnum.PayStatus;
import com.lsh.payment.core.service.payTask.PayTaskService;
import com.lsh.payment.core.service.payment.impl.PayQueryBaseService;
import com.lsh.payment.core.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/9.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service
public class ReceiveNotifyService {

    @Autowired
    private PayTaskService payTaskService;

    @Autowired
    private PayQueryBaseService payQueryBaseService;

    /**
     * 查询任务
     *
     * @param minutes 分钟
     * @return list
     */
    public List<PayTask> selectTasks(int minutes) {

        List<PayTask> payTaskList = payTaskService.selectTasksForNotify(DateUtil.getDateTimeBefore(minutes));
        if (CollectionUtils.isEmpty(payTaskList)) {
            payTaskList = new ArrayList<>();
        }

        return payTaskList;
    }

    /**
     * 处理任务
     *
     * @param payTask  处理任务
     * @return list
     */
    public boolean dealPayTask(PayTask payTask) {

        PaymentQueryRequest paymentQueryRequest = new PaymentQueryRequest();
        paymentQueryRequest.setPay_payment_no(payTask.getPayPaymentNo());

        QueryContent queryContent = payQueryBaseService.queryPayStatus(paymentQueryRequest);
        //支付中,查询次数加一
        if (queryContent != null  && queryContent.getPayCode() != null && queryContent.getPayCode() == PayStatus.PAYING.getValue()) {
            payTaskService.addQueryTimes(payTask.getPayId());
        }

        return true;
    }

}
