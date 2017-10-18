package com.lsh.payment.core.service.refund;

import com.lsh.payment.core.model.refund.RefundTask;

import java.util.List;

/**
 * Project Name: lsh-payment
 * Created by peter on 17/7/3.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public interface IRefundTaskService {

    int addRefundTask(RefundTask refundTask);


    List<RefundTask> selectRefundTask4Task(RefundTask refundTask);

    int updateRefundTask(Long id,int status);

    int updateRefundTask(String refId,int status);
}
