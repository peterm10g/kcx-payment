package com.lsh.payment.core.dao.payment;


import com.lsh.payment.core.dao.MyBatisRepository;
import com.lsh.payment.core.model.payment.PayDeal;

import java.util.Date;
import java.util.List;
import java.util.Map;

@MyBatisRepository
public interface PayDealDao {
    /**
     *
     * @param id  主键
     * @return int
     */
    int deleteByPrimaryKey(Long id);

    /**
     *
     * @param record PayDeal
     * @return int
     */
    int insert(PayDeal record);

    /**
     *
     * @param record PayDeal
     * @return int
     */
    int insertSelective(PayDeal record);

    /**
     *
     * @param recordList recordList
     * @return int
     */
    int insertBatch(List<PayDeal> recordList);

    /**
     *
     * @param id PayDeal
     * @return PayDeal
     */
    PayDeal selectByPrimaryKey(Long id);

    /**
     *
     * @param record PayDeal
     * @return int
     */
    int updateByPrimaryKeySelective(PayDeal record);

    /**
     *
     * @param record PayDeal
     * @return int
     */
    int updateByPrimaryKey(PayDeal record);

    /**
     *
     * @param record PayDeal
     * @return int
     */
    int updateByPayIdSelective(PayDeal record);

    /**
     *
     * @param params params
     * @return
     */
    List<PayDeal> selectByParams(Map<String,Object> params);

    /**
     *
     * @param params recordList
     * @return int
     */
    int insert2History(Map<String,Object> params);

    int delete2History(Map<String,Object> params);

    int historyCount(Date time);

}