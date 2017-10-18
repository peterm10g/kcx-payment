package com.lsh.payment.worker.task;

import com.dangdang.ddframe.job.api.JobExecutionSingleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.dataflow.AbstractBatchSequenceDataFlowElasticJob;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.Async.EmailModel;
import com.lsh.payment.core.service.AsyncService.AsyncEvent;
import com.lsh.payment.core.strategy.billStrategy.BillStrategyContext;
import com.lsh.payment.core.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/16.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service
public class DownLoadBillJop extends AbstractBatchSequenceDataFlowElasticJob<String> {

    private Logger logger = LoggerFactory.getLogger(DownLoadBillJop.class);

    @Value("${to.user.email}")
    private String userEmail;


    /**
     * 分片获取数据(分支付渠道)
     *
     * @param jobContext 任务上下文
     * @return 数据列表
     */
    public List<String> fetchData(JobExecutionSingleShardingContext jobContext) {
        String payType = jobContext.getShardingItemParameter();
        logger.info(" 支付类型是 : " + payType);

        return Collections.singletonList(payType);
    }

    /**
     * 按分片处理任务
     *
     * @param jobContext 任务上下文
     * @param list       任务列表
     * @return 执行结果
     */
    public int processData(JobExecutionSingleShardingContext jobContext, List<String> list) {

        try {
            if (list == null || list.isEmpty() || StringUtils.isBlank(list.get(0))) {
                return 1;
            }

            String payType = list.get(0);
            logger.info(" payType is : " + payType);

            BillStrategyContext billStrategyContext = new BillStrategyContext();
            billStrategyContext.downloadBill(payType);
        } catch (BusinessException e) {
            //TODO 发送邮件
            String[] userEmails = userEmail.split(",");
            String title = "对账单下载预警";

            AsyncEvent.post(new EmailModel(e.getMessage() + "\n" + DateUtil.defaultTimestamp(new Date()), title, userEmails));
            logger.error("业务异常:" + e.getCode() + e.getMessage());
        } catch (Exception e) {
            logger.error("服务端异常", e);
        }

        return 1;
    }


    public static void main(String[] args) {
        BillStrategyContext billStrategyContext = new BillStrategyContext();
//        billStrategyContext.downloadBill("WXQF");
        billStrategyContext.downloadBill("WXXY");


//        String billDate = "2017-08-08";
//        String payName = PayChannel.ALIPAY.getName();
//        int billType = 0;
////
//        BillStrategy billStrategy = BillStrategyFactory.getInstance().creator(payName);
//        billStrategy.downloadBillByDate(billDate, billType);

    }
}
