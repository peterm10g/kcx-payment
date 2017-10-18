package com.lsh.payment.worker.service;

import com.lsh.payment.core.model.payNotifyTmsTask.PayNotifyTmsTask;
import com.lsh.payment.core.service.businessNotify.BusinessNotifyService;
import com.lsh.payment.core.service.payment.impl.PaymentNotifyTaskService;
import com.lsh.payment.worker.task.PaymentNotifyJop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/9.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service
public class PaymentNotifyService {

    private static Logger logger = LoggerFactory.getLogger(PaymentNotifyJop.class);


    @Autowired
    private PaymentNotifyTaskService paymentNotifyTaskService;

    @Autowired
    private BusinessNotifyService businessNotifyService;

    /**
     *
     * @return
     */
    public List<PayNotifyTmsTask> selectTasks(){

        List<PayNotifyTmsTask> payNotifyTmsTaskList = paymentNotifyTaskService.selectTasks(0);
        if(CollectionUtils.isEmpty(payNotifyTmsTaskList)){
            logger.info("需要回调的支付结果记录为空");
            return Collections.emptyList();
        }

        return payNotifyTmsTaskList;
    }

    /**
     *
     * @return
     */
    public boolean dealNotify(PayNotifyTmsTask payNotifyTmsTask){

        if(businessNotifyService.dealNotify(payNotifyTmsTask) > 0){
            return true;
        }

        return false;
    }

}
