package com.lsh.payment.worker.model;

import com.lsh.payment.core.model.PayTask.PayTask;
import com.lsh.payment.core.model.payNotifyTmsTask.PayNotifyTmsTask;

import java.util.List;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/13.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class PaymentMonitor {

    private List<PayTask> payTaskList;


    private List<PayNotifyTmsTask> payNotifyTmsTaskList;


    public List<PayTask> getPayTaskList() {
        return payTaskList;
    }

    public void setPayTaskList(List<PayTask> payTaskList) {
        this.payTaskList = payTaskList;
    }

    public List<PayNotifyTmsTask> getPayNotifyTmsTaskList() {
        return payNotifyTmsTaskList;
    }

    public void setPayNotifyTmsTaskList(List<PayNotifyTmsTask> payNotifyTmsTaskList) {
        this.payNotifyTmsTaskList = payNotifyTmsTaskList;
    }
}
