package com.lsh.payment.core.dao.payLog;


import com.lsh.payment.core.dao.MyBatisRepository;
import com.lsh.payment.core.model.payLog.PayLog;

@MyBatisRepository
public interface PayLogDao {
    /**
     *
     * @param id
     * @return
     */
    int deleteByPrimaryKey(Long id);

    /**
     *
     * @param record
     * @return
     */
    int insert(PayLog record);

    /**
     *
     * @param record
     * @return
     */
    int insertSelective(PayLog record);

    /**
     *
     * @param id
     * @return
     */
    PayLog selectByPrimaryKey(Long id);

    /**
     *
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(PayLog record);

    /**
     *
     * @param record
     * @return
     */
    int updateByPrimaryKey(PayLog record);
}