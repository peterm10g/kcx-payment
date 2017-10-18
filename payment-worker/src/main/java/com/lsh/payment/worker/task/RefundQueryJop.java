package com.lsh.payment.worker.task;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.lsh.payment.core.model.refund.RefundTask;
import com.lsh.payment.worker.service.RefundTaskJopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/9.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Component
public class RefundQueryJop extends AbstractSimpleElasticJob {

    private static Logger logger = LoggerFactory.getLogger(RefundQueryJop.class);

    @Autowired
    private RefundTaskJopService refundTaskJopService;

    private AtomicBoolean migration = new AtomicBoolean(false);


    public void process(JobExecutionMultipleShardingContext jobContext) {
       List<RefundTask> refundTaskList = refundTaskJopService.getRefundTasks();

        if(refundTaskList == null){
            return ;
        }

        for(RefundTask refundTask : refundTaskList){
            logger.info("****** 处理refundTask 开始");
            refundTaskJopService.dealRefundTask(refundTask);
            logger.info("****** 处理refundTask 结束");
        }

    }


}
