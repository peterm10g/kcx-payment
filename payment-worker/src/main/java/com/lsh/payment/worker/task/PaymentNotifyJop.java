package com.lsh.payment.worker.task;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.lsh.payment.core.model.payNotifyTmsTask.PayNotifyTmsTask;
import com.lsh.payment.worker.service.PaymentNotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/9.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Component
public class PaymentNotifyJop extends AbstractSimpleElasticJob {

    private static Logger logger = LoggerFactory.getLogger(PaymentNotifyJop.class);

    @Autowired
    private PaymentNotifyService paymentNotifyService;


    public void process(JobExecutionMultipleShardingContext jobContext) {

        List<PayNotifyTmsTask> payNotifyTmsTaskList = paymentNotifyService.selectTasks();

        if(CollectionUtils.isEmpty(payNotifyTmsTaskList)){
            logger.info("Jop payNotifyTmsTaskList is empty");
            return ;
        }

        for (PayNotifyTmsTask payNotifyTask : payNotifyTmsTaskList) {
            logger.info("Jop 处理回调信息 : " + JSON.toJSONString(payNotifyTask));
            paymentNotifyService.dealNotify(payNotifyTask);
        }
    }


}
