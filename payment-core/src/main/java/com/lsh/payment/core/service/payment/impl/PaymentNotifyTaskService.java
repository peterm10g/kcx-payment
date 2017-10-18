package com.lsh.payment.core.service.payment.impl;

import com.lsh.payment.core.dao.payNotifyTmsTask.PayNotifyTmsTaskDao;
import com.lsh.payment.core.model.payNotifyTmsTask.PayNotifyTmsTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/9.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service
@Transactional(readOnly = true)
public class PaymentNotifyTaskService {

    @Autowired
    private PayNotifyTmsTaskDao payNotifyTmsTaskDao;

    /**
     *
     * @param status 支付记录状态
     * @return       返回需要处理的定时任务
     */
    public List<PayNotifyTmsTask> selectTasks(int status) {

        Map<String,Object> paraMap = new HashMap<>();

        paraMap.put("status",status);

        paraMap.put("emailStatus",0);//0 未发送邮件,1已发送邮件

        return payNotifyTmsTaskDao.selectTaskByStatus(paraMap);
    }

    /**
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int deletePayTasks(int status) {

        return payNotifyTmsTaskDao.deleteByStatus(status);
    }

    /**
     *
     *
     */
    @Transactional(rollbackFor = Exception.class)
    public int updatePayTaskEmailStatus(String payIdStr){

        Map<String,Object> paraMap = new HashMap<>();

        paraMap.put("ids",payIdStr);

        return payNotifyTmsTaskDao.updateEmailStatusById(paraMap);
    }


}
