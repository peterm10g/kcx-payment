package com.lsh.payment.core.dao.refund;


import com.lsh.payment.core.dao.MyBatisRepository;
import com.lsh.payment.core.model.refund.RefundTask;

import java.util.List;

@MyBatisRepository
public interface RefundTaskDao {

    int deleteByPrimaryKey(Long id);

    int insert(RefundTask record);

    int insertSelective(RefundTask record);

    RefundTask selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RefundTask record);

    int updateByPrimaryKey(RefundTask record);

    List<RefundTask> selectByRecord(RefundTask record);

    int updateByRefId(RefundTask record);
}