package com.lsh.payment.core.service.alipay.impl;

import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.payEnum.OperateStatus;
import com.lsh.payment.core.model.payEnum.PayStatus;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.service.alipay.IAliNotifyService;
import com.lsh.payment.core.service.payment.impl.PayBaseService;
import com.lsh.payment.core.service.payment.impl.PayDealService;
import com.lsh.payment.core.strategy.config.AliPayConfig;
import com.lsh.payment.core.util.DateUtil;
import com.lsh.payment.core.util.HttpClientUtils;
import com.lsh.payment.core.util.PayAssert;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/11/3
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.service.alipay
 * desc:
 */
@Service
@Transactional(readOnly = true)
public class AliNotifyServiceImpl implements IAliNotifyService {
    private Logger logger = LoggerFactory.getLogger(AliNotifyServiceImpl.class);

    @Autowired
    private PayDealService payDealService;

    @Autowired
    private PayBaseService payBaseService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PayDeal notifyHandle(Map<String, String> paramsMap) throws Exception {
        PayDeal payDeal;

        //回调状态支付中不处理
        if (!paramsMap.get("trade_status").equals(AliPayConfig.TRADE_SUCCESS)
                && !paramsMap.get("trade_status").equals(AliPayConfig.TRADE_CLOSED)) {
            logger.info("订单:" + paramsMap.get("out_trade_no") + "支付宝返回状态:" + paramsMap.get("trade_status"));
            return null;
        }

        //校验报文
        if (!checkParams(paramsMap)) {
            logger.error("校验数据失败:" + paramsMap);
            throw new BusinessException(ExceptionStatus.E1002001.getCode(), ExceptionStatus.E1002001.getMessage());
        }

        boolean signVerified = false;
        if(StringUtils.isNotBlank(paramsMap.get("total_fee"))){
            //调用SDK验证签名
            signVerified = AlipayCheck.rsaCheckV1(paramsMap, AliPayConfig.public_key, AliPayConfig.input_charset);
        }else if(StringUtils.isNotBlank(paramsMap.get("total_amount"))){
            //调用SDK验证签名
            signVerified = AlipayCheck.rsaCheckV1(paramsMap, AliPayConfig.alipay_public_key, AliPayConfig.input_charset);
        }

        if (!signVerified) {
            logger.info("验签失败 [" + paramsMap + "]");
            throw new BusinessException(ExceptionStatus.E1002001.getCode(), ExceptionStatus.E1002001.getMessage());
        }

        //查询
        payDeal = payDealService.queryPayDealByPayPaymentNo(paramsMap.get("out_trade_no"));

        if (payDeal == null) {
            logger.error("没有查到记录:", paramsMap.get("out_trade_no"));
            throw new BusinessException(ExceptionStatus.E2001004.getCode(), ExceptionStatus.E2001004.getMessage());
        }
        if ((payDeal.getPayStatus() != PayStatus.PAY_SUCCESS.getValue() && payDeal.getPayStatus() != PayStatus.PAY_FAIL.getValue()) || payDeal.getOperateStatus() != OperateStatus.PAYMENT_CALLBACK.getCode()) {
            //非终态处理
            //校验
            //校验total_amount 是否为订单金额
            String getAmount = paramsMap.get("total_amount");
            if(StringUtils.isBlank(getAmount)){
                getAmount = paramsMap.get("total_fee");
            }

            if(StringUtils.isBlank(getAmount)){
                logger.error("校验数据失败,金额为空:" + paramsMap + ",订单金额:" + payDeal.getRequestAmount());
                throw new BusinessException(ExceptionStatus.E1002001.getCode(), ExceptionStatus.E1002001.getMessage());
            }

            if (payDeal.getRequestAmount().compareTo(new BigDecimal(getAmount)) == 0) {
                //更新
                PayDeal payDealUpd = new PayDeal();
                payDealUpd.setChannelTransaction(paramsMap.get("trade_no"));
                payDealUpd.setPayId(payDeal.getPayId());
                payDealUpd.setPayTime(DateUtil.parsePayDate_1(paramsMap.get("gmt_payment")));
                payDealUpd.setDoneTime(new Date());
                payDealUpd.setOperateStatus(OperateStatus.PAYMENT_CALLBACK.getCode());
                payDealUpd.setReceiveAmount(payDeal.getRequestAmount());
                if (paramsMap.get("trade_status").equals(AliPayConfig.TRADE_SUCCESS) || paramsMap.get("trade_status").equals(AliPayConfig.TRADE_FINISHED)) {
                    payDealUpd.setPayStatus(PayStatus.PAY_SUCCESS.getValue());
                }
                if (paramsMap.get("trade_status").equals(AliPayConfig.TRADE_CLOSED)) {
                    payDealUpd.setPayStatus(PayStatus.PAY_FAIL.getValue());
                }

                payBaseService.updPayDeal(payDealUpd, payDeal);
            } else {
                logger.error("校验数据失败,金额不一致:" + paramsMap + ",订单金额:" + payDeal.getRequestAmount());
                throw new BusinessException(ExceptionStatus.E1002001.getCode(), ExceptionStatus.E1002001.getMessage());
            }
        } else {
            logger.info("支付宝支付信息重复回调");
        }

        return payDeal;
    }


    /**
     * 参数校验
     *
     * @param paramsMap  校验参数
     * @return           boolean
     */
    private boolean checkParams(Map<String, String> paramsMap) {

        PayAssert.notNull(paramsMap, ExceptionStatus.E1002002.getCode(), ExceptionStatus.E1002002.getMessage());
        //notify_id

        PayAssert.notNull(paramsMap.get("trade_no"), ExceptionStatus.E1002002.getCode(), "trade_no " + ExceptionStatus.E1002002.getMessage());
        PayAssert.notNull(paramsMap.get("trade_status"), ExceptionStatus.E1002002.getCode(), "trade_status " + ExceptionStatus.E1002002.getMessage());
        PayAssert.notNull(paramsMap.get("sign"), ExceptionStatus.E1002002.getCode(), "sign" + ExceptionStatus.E1002002.getMessage());
        PayAssert.notNull(paramsMap.get("sign_type"), ExceptionStatus.E1002002.getCode(), "sign_type " + ExceptionStatus.E1002002.getMessage());
        PayAssert.notNull(paramsMap.get("seller_id"), ExceptionStatus.E1002002.getCode(), "seller_id " + ExceptionStatus.E1002002.getMessage());
        PayAssert.notNull(paramsMap.get("notify_type"), ExceptionStatus.E1002002.getCode(), "notify_type " + ExceptionStatus.E1002002.getMessage());
//        PayAssert.notNull(paramsMap.get("total_fee"), ExceptionStatus.E1002002.getCode(), "total_fee " + ExceptionStatus.E1002002.getMessage());
        PayAssert.notNull(paramsMap.get("out_trade_no"), ExceptionStatus.E1002002.getCode(), "out_trade_no " + ExceptionStatus.E1002002.getMessage());

        String getAmount = paramsMap.get("total_fee");
        if(StringUtils.isBlank(getAmount)){
            getAmount = paramsMap.get("total_amount");
        }

        PayAssert.notNull(getAmount, ExceptionStatus.E1002002.getCode(), "total_fee ,total_amount" + ExceptionStatus.E1002002.getMessage());

        //根据notify_id校验是回调否是来自支付宝
        //https://mapi.alipay.com/gateway.do?service=notify_verify&partner=2088121287862492&notify_id=8acd480708a3e139c187b8e57573bf1l0m
        //PayAssert.notNull(paramsMap.get("notify_id"), ExceptionStatus.E1002002.getCode(),"out_trade_no " +  ExceptionStatus.E1002002.getMessage());
        Map<String,Object> aliPayRequestMap = new HashMap<>(5);
        aliPayRequestMap.put("service", AliPayConfig.notify_service);
        aliPayRequestMap.put("partner", AliPayConfig.partner);
        aliPayRequestMap.put("notify_id", paramsMap.get("notify_id"));
        String aliPayResponse = HttpClientUtils.doPost(AliPayConfig.ALIPAY_GATEWAY_NEW, aliPayRequestMap);

        logger.error("HttpClientUtils aliPayResponse:" + aliPayResponse);
        if (!"true".equals(aliPayResponse)) {
            logger.error("校验notify_id错误,aliPayResponse:" + aliPayResponse);
            return false;
        }

        //校验seller_id是否为out_trade_no这笔单据的对应的操作方
        if (!AliPayConfig.seller_id.equals(paramsMap.get("seller_id"))) {
            logger.error("校验seller_id不一致,返回的seller_id:" + paramsMap.get("seller_id") + "支付seller_id:" + AliPayConfig.seller_id);
            return false;
        }
        if (!AliPayConfig.sign_type.equals(paramsMap.get("sign_type"))) {
            logger.error("校验sign_type不一致,返回的sign_type:" + paramsMap.get("sign_type") + "支付sign_type:" + AliPayConfig.sign_type);
            return false;
        }
        if (!AliPayConfig.notify_type.equals(paramsMap.get("notify_type"))) {
            logger.error("校验notify_type不一致,返回的notify_type:" + paramsMap.get("notify_type") + "支付notify_type:" + AliPayConfig.notify_type);
            return false;
        }

        return true;
    }
}
