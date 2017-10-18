package com.lsh.payment.core.dao.refund;


import com.lsh.payment.core.dao.MyBatisRepository;
import com.lsh.payment.core.model.refund.PayRefund;

import java.util.List;
import java.util.Map;

@MyBatisRepository
public interface PayRefundDao {

    int deleteByPrimaryKey(Long id);

    int insert(PayRefund record);

    int insertSelective(PayRefund record);

    PayRefund selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PayRefund record);

    int updateByPrimaryKey(PayRefund record);

    int updateByPayRefundIdSelective(PayRefund record);

    List<PayRefund> selectByParams(Map<String,Object> params);
}