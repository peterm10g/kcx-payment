package com.lsh.payment.worker.task;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.lsh.payment.core.model.PayTask.PayTask;
import com.lsh.payment.worker.service.ReceiveNotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/10.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Component
public class ChannelNotifyJop extends AbstractSimpleElasticJob {

    private static final Logger log = LoggerFactory.getLogger(AbstractSimpleElasticJob.class);

    @Autowired
    private ReceiveNotifyService receiveNotifyService;

    @Value("${deal.max.times}")
    private int maxTimes;


    public void process(JobExecutionMultipleShardingContext jobContext) {

        try {
            List<PayTask> payTaskList = receiveNotifyService.selectTasks(maxTimes);

            if (CollectionUtils.isEmpty(payTaskList)) {
                log.info("Jop payTaskList is empty");
                return;
            }

            for (PayTask payTask : payTaskList) {
                log.info(" deal payTask is : " + JSON.toJSONString(payTask));
                receiveNotifyService.dealPayTask(payTask);
            }
        } catch (Exception ex) {
            log.info("支付渠道通知定时任务异常", ex);
        }
    }


}
