package com.lsh.payment.core.dao.payTask;


import com.lsh.payment.core.dao.MyBatisRepository;
import com.lsh.payment.core.model.PayTask.PayTask;

import java.util.List;
import java.util.Map;

@MyBatisRepository
public interface PayTaskDao {

    int deleteByPrimaryKey(Long id);

    int deleteByPayId(String payId);

    int insert(PayTask record);

    int insertSelective(PayTask record);

    PayTask selectByPrimaryKey(Long id);

    List<PayTask> selectByParaMap(Map<String,Object> paraMap);

    int updateByPrimaryKeySelective(PayTask record);

    int updateByPayIdSelective(PayTask record);

    int updateByPrimaryKey(PayTask record);

    int addQueryTimesByPayId(PayTask record);

    int updateEmailStatusById(Map<String,Object> paraMap);
}