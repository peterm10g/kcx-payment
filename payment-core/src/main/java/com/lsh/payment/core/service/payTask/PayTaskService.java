package com.lsh.payment.core.service.payTask;

import com.lsh.payment.core.dao.payTask.PayTaskDao;
import com.lsh.payment.core.model.PayTask.PayTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/1.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Component
public class PayTaskService {

    private static Logger logger = LoggerFactory.getLogger(PayTaskService.class);

    @Autowired
    private PayTaskDao payTaskDao;

    /**
     * @param payId
     */
    public void deletePayTask(String payId) {

        payTaskDao.deleteByPayId(payId);
    }

    /**
     * @param payTask
     */
//    @Transactional(rollbackFor = Exception.class)
    public void addPayTask(PayTask payTask) {

        payTaskDao.insertSelective(payTask);
    }

    /**
     * @param payId
     */
    @Transactional(rollbackFor = Exception.class)
    public void addQueryTimes(String payId) {

        PayTask payTask = new PayTask();
        payTask.setPayId(payId);

        payTaskDao.addQueryTimesByPayId(payTask);
    }

    /**
     *
     *
     */
    @Transactional(readOnly = true)
    public List<PayTask> selectTasksForNotify(Date dateTime) {

        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("createdAt", dateTime);
        paraMap.put("queryTimes", 10);

        return payTaskDao.selectByParaMap(paraMap);
    }


    /**
     *
     *
     */
    @Transactional(readOnly = true)
    public List<PayTask> selectTasksForMonitor(int maxTimes) {

        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("maxQueryTimes", maxTimes);
        paraMap.put("emailStatus", 0);

        return payTaskDao.selectByParaMap(paraMap);
    }


    /**
     *
     *
     */
    @Transactional(rollbackFor = Exception.class)
    public int updatePayTaskEmailStatus(String payIdStr) {

        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("ids", payIdStr);

        return payTaskDao.updateEmailStatusById(paraMap);
    }

}
