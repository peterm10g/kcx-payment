package com.lsh.payment.worker.task;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.lsh.payment.worker.service.PayDealHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/9.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Component
public class PayDealHistoryJop extends AbstractSimpleElasticJob {

    private static Logger logger = LoggerFactory.getLogger(PayDealHistoryJop.class);

    @Autowired
    private PayDealHistoryService payDealHistoryService;

    private AtomicBoolean migration = new AtomicBoolean(false);


    public void process(JobExecutionMultipleShardingContext jobContext) {
        if (migration.compareAndSet(false, true)) {
//            payDealHistoryService.payDeal2History();
        }else{
            logger.info("数据已迁移,不能重复迁移");
        }
    }


}
