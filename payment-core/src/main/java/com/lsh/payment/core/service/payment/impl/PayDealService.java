package com.lsh.payment.core.service.payment.impl;

import com.lsh.base.common.utils.CollectionUtils;
import com.lsh.payment.core.dao.payment.PayDealDao;
import com.lsh.payment.core.model.payment.PayDeal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/28.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Component
@Transactional(readOnly = true)
public class PayDealService {

    private static Logger logger = LoggerFactory.getLogger(PayDealService.class);

    @Autowired
    private PayDealDao payDealDao;

    /**
     * 添加支付记录
     * @param payDeal 支付记录
     * @return        操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int addPayDeal(PayDeal payDeal) {
        return payDealDao.insertSelective(payDeal);
    }

    /**
     * 修改支付记录
     * @param payDeal 支付记录
     * @return        操作结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int updatePayDeal(PayDeal payDeal) {
        return payDealDao.updateByPayIdSelective(payDeal);
    }

    /**
     * 查询支付记录
     * @param params 参数
     * @return       返回结果
     */
    public List<PayDeal> queryPayDeals(Map<String, Object> params) {
        return payDealDao.selectByParams(params);
    }


    /**
     * 查询支付记录列表
     * @param tradeId 交易id
     * @return        支付记录列表
     */
    public List<PayDeal> queryPayDealByTradeId(String tradeId) {
        if(StringUtils.isBlank(tradeId)){
            logger.info("tradeId is null ");
            return null;
        }

        List<PayDeal> payDeals = payDealDao.selectByParams(Collections.<String,Object>singletonMap("tradeId", tradeId));
        if (CollectionUtils.isEmpty(payDeals)) {
            logger.info("tradeId [{}] 对应的支付记录不存在 ",tradeId);
            return null;
        }
        return payDeals;
    }

    /**
     * 查询支付记录
     * @param payPaymentNo  支付平台流水号
     * @return              支付记录
     */
    public PayDeal queryPayDealByPayPaymentNo(String payPaymentNo) {

        if(StringUtils.isBlank(payPaymentNo)){
            logger.info("payPaymentNo is null ");
            return null;
        }

        List<PayDeal> payDeals = payDealDao.selectByParams(Collections.<String,Object>singletonMap("payPaymentNo", payPaymentNo));
        if (CollectionUtils.isEmpty(payDeals)) {
            logger.info("payPaymentNo [{}] 对应的支付记录不存在 ",payPaymentNo);
            return null;
        }
        return CollectionUtils.getFirst(payDeals);
    }

    /**
     * 查询支付记录
     * @param channelTransaction 第三方支付流水号
     * @return                   支付记录
     */
    public PayDeal queryPayDealByChannelTransaction(String channelTransaction) {
        if(StringUtils.isBlank(channelTransaction)){
            logger.info("channelTransaction is null ");
            return null;
        }

        List<PayDeal> payDeals = payDealDao.selectByParams(Collections.<String,Object>singletonMap("channelTransaction", channelTransaction));
        if (CollectionUtils.isEmpty(payDeals)) {
            logger.info("channelTransaction [{}] 对应的支付记录不存在 ",channelTransaction);
            return null;
        }
        return CollectionUtils.getFirst(payDeals);
    }

    /**
     * 批量插入支付结果
     * @param payDealList 支付结果列表
     * @return            结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int insertBatch(List<PayDeal> payDealList) {
        return payDealDao.insertBatch(payDealList);
    }


    /**
     * 数据迁移
     * @param  params 支付结果列表
     * @return            结果
     */
    @Transactional(rollbackFor = Exception.class)
    public int payDeal2History(Map<String,Object> params) {

        return  payDealDao.insert2History(params);
    }

    @Transactional(rollbackFor = Exception.class)
    public int delete2History(Map<String,Object> params) {

        return  payDealDao.delete2History(params);
    }

    public int historyCount(Date time) {

      return payDealDao.historyCount(time);
    }

}
