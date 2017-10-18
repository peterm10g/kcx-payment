package com.lsh.payment.worker.task;

import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
import com.lsh.payment.core.model.Async.EmailModel;
import com.lsh.payment.core.model.PayTask.PayTask;
import com.lsh.payment.core.model.payNotifyTmsTask.PayNotifyTmsTask;
import com.lsh.payment.core.service.AsyncService.AsyncEvent;
import com.lsh.payment.core.service.payTask.PayTaskService;
import com.lsh.payment.core.service.payment.impl.PaymentNotifyTaskService;
import com.lsh.payment.core.util.DateUtil;
import com.lsh.payment.worker.model.PaymentMonitor;
import com.lsh.payment.worker.service.PaymentMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Date;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/13.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Component
public class PaymentMonitorJop extends AbstractSimpleElasticJob {

    private static Logger logger = LoggerFactory.getLogger(PaymentMonitorJop.class);

    @Autowired
    private PaymentMonitorService paymentMonitorService;

    @Value("${to.user.email}")
    private String userEmail;

    @Autowired
    private PayTaskService payTaskService;

    @Autowired
    private PaymentNotifyTaskService paymentNotifyTaskService;


    public void process(JobExecutionMultipleShardingContext jobExecutionMultipleShardingContext) {

        PaymentMonitor paymentMonitor = paymentMonitorService.monitorPaymentTask();

//        StringBuffer payTaskBuffer = new StringBuffer("查询第三方支付结果记录: ").append(System.getProperty("line.separator"));

        boolean sendFlag = false;

        StringBuffer payTaskPayIdBuffer = new StringBuffer("(");
        StringBuffer notifyTaskPayIdBuffer = new StringBuffer("(");
        boolean paySendFlag = false;
        boolean notifySendFlag = false;
        if (!CollectionUtils.isEmpty(paymentMonitor.getPayTaskList())) {

            for (PayTask payTask : paymentMonitor.getPayTaskList()) {

//                payTaskBuffer.append("支付平台流水号").append(" : ").append(payTask.getPayPaymentNo()).append(" , ")
//                        .append("业务平台订单号").append(" : ").append(payTask.getTradeId()).append(" , ")
//                        .append("查询次数").append(" : ").append(payTask.getQueryTimes()).append(", ")
//                        .append("支付结果").append(" : ").append("支付中")
//                        .append(System.getProperty("line.separator"));

                payTaskPayIdBuffer.append(payTask.getId()).append(",");
                paySendFlag = true;

//                if (!sendFlag) {
//                    sendFlag = true;
//                }
            }

//            payTaskBuffer.append(System.getProperty("line.separator"));
        }

        StringBuffer notifyTaskBuffer = new StringBuffer("业务方支付结果通知失败记录: ").append(System.getProperty("line.separator"));

        if (!CollectionUtils.isEmpty(paymentMonitor.getPayNotifyTmsTaskList())) {

            for (PayNotifyTmsTask payNotifyTmsTask : paymentMonitor.getPayNotifyTmsTaskList()) {

                int notifyStatus = payNotifyTmsTask.getStatus();
                String notifyStatusMessage = "未通知";
                if (notifyStatus == 1) {
                    notifyStatusMessage = "通知成功";
                } else if (notifyStatus == 2) {
                    notifyStatusMessage = "通知失败";
                }

                notifyTaskBuffer.append("支付平台流水号").append(" : ").append(payNotifyTmsTask.getPayPaymentNo()).append(" , ")
                        .append("业务平台订单号").append(" : ").append(payNotifyTmsTask.getTradeId()).append(", ")
                        .append("通知次数").append(" : ").append(payNotifyTmsTask.getNotifyTimes()).append(", ")
                        .append("通知结果").append(" : ").append(notifyStatusMessage)
                        .append(System.getProperty("line.separator"));

                notifyTaskPayIdBuffer.append(payNotifyTmsTask.getId()).append(",");
                notifySendFlag = true;

                if (!sendFlag) {
                    sendFlag = true;
                }
            }

            notifyTaskBuffer.append(System.getProperty("line.separator"));
        }

        StringBuffer emailBuffer = new StringBuffer();

        emailBuffer.append(notifyTaskBuffer.toString());
//                .append(payTaskBuffer.toString());

        String[] userEmails = userEmail.split(",");

        logger.info("发送邮件地址:" + Arrays.toString(userEmails));

        String title = "支付系统监控_" + DateUtil.defaultDate(new Date());

        // 发送邮件
        if (sendFlag) {
            //异步邮件发送
//            sendEmailService.sendAsyn(emailBuffer.toString(), title, userEmails);
            AsyncEvent.post(new EmailModel(emailBuffer.toString(), title, userEmails));
            //更新发送邮件状态
            if (paySendFlag) {
                payTaskPayIdBuffer.deleteCharAt(payTaskPayIdBuffer.length() - 1);
                payTaskPayIdBuffer.append(")");
                payTaskService.updatePayTaskEmailStatus(payTaskPayIdBuffer.toString());
            }
            if (notifySendFlag) {
                notifyTaskPayIdBuffer.deleteCharAt(notifyTaskPayIdBuffer.length() - 1);
                notifyTaskPayIdBuffer.append(")");
                paymentNotifyTaskService.updatePayTaskEmailStatus(notifyTaskPayIdBuffer.toString());
            }

        }

        //删除回调成功提醒
        paymentMonitorService.deletePaymentNitifyTask();
    }


}
