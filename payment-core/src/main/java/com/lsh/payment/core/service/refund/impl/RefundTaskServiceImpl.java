package com.lsh.payment.core.service.refund.impl;

import com.lsh.payment.core.dao.refund.RefundTaskDao;
import com.lsh.payment.core.model.refund.RefundTask;
import com.lsh.payment.core.service.refund.IRefundTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Project Name: lsh-payment
 * Created by peter on 17/7/3.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service
@Transactional(readOnly = true)
public class RefundTaskServiceImpl implements IRefundTaskService {

    @Autowired
    private RefundTaskDao refundTaskDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addRefundTask(RefundTask refundTask) {

        return refundTaskDao.insertSelective(refundTask);
    }

    @Override
    public List<RefundTask> selectRefundTask4Task(RefundTask refundTask) {
        return refundTaskDao.selectByRecord(refundTask);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRefundTask(Long id, int status) {
        RefundTask refundTask = new RefundTask();
        refundTask.setId(id);
        refundTask.setStatus(status);
        return refundTaskDao.updateByPrimaryKeySelective(refundTask);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRefundTask(String refId, int status) {
        RefundTask refundTask = new RefundTask();
        refundTask.setRefId(refId);
        refundTask.setStatus(status);

        return refundTaskDao.updateByRefId(refundTask);
    }
}
