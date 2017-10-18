package com.lsh.payment.worker.service;

import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.model.PayTask.PayTask;
import com.lsh.payment.core.model.payNotifyTmsTask.PayNotifyTmsTask;
import com.lsh.payment.core.service.payTask.PayTaskService;
import com.lsh.payment.core.service.payment.impl.PaymentNotifyTaskService;
import com.lsh.payment.worker.model.PaymentMonitor;
import com.lsh.payment.worker.task.PaymentNotifyJop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/13.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service
public class PaymentMonitorService {

    private static Logger logger = LoggerFactory.getLogger(PaymentNotifyJop.class);

    int maxTimes = 10;

    @Autowired
    private PayTaskService payTaskService;

    @Autowired
    private PaymentNotifyTaskService paymentNotifyTaskService;


    /**
     *
     * @return
     */
    public PaymentMonitor monitorPaymentTask(){

        List<PayTask> payTaskList =  payTaskService.selectTasksForMonitor(maxTimes);
        List<PayNotifyTmsTask> payNotifyTmsTaskList = paymentNotifyTaskService.selectTasks(BusiConstant.TMS_TASK_STATUS_FAIL);

        PaymentMonitor paymentMonitor = new PaymentMonitor();
        paymentMonitor.setPayNotifyTmsTaskList(payNotifyTmsTaskList);
        paymentMonitor.setPayTaskList(payTaskList);

        return paymentMonitor;
    }

    /**
     *
     * @return
     */
    public boolean deletePaymentNitifyTask(){

        paymentNotifyTaskService.deletePayTasks(BusiConstant.TMS_TASK_STATUS_SUCCESS);

        return true;
    }




}
