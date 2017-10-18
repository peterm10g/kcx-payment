package com.lsh.payment.core.dao.payNotifyTmsTask;

import com.lsh.payment.core.dao.MyBatisRepository;
import com.lsh.payment.core.model.payNotifyTmsTask.PayNotifyTmsTask;

import java.util.List;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/11/8
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.dao.payNotifyTmsTask
 * desc:
 */
@MyBatisRepository
public interface PayNotifyTmsTaskDao {

    int insertSelective(PayNotifyTmsTask record);

    PayNotifyTmsTask selectByPrimaryKey(Long id);

    List<PayNotifyTmsTask> selectTaskByStatus(Map<String,Object> paraMap);

    int updateByPrimaryKeySelective(PayNotifyTmsTask record);

    int deleteByStatus(Integer status);

    int updateEmailStatusById(Map<String,Object> paraMap);
}


