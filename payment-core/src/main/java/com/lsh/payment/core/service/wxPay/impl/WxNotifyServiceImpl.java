package com.lsh.payment.core.service.wxPay.impl;

import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.payEnum.OperateStatus;
import com.lsh.payment.core.model.payEnum.PayStatus;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.service.payment.impl.PayBaseService;
import com.lsh.payment.core.service.payment.impl.PayDealService;
import com.lsh.payment.core.service.wxPay.IWxNotifyService;
import com.lsh.payment.core.util.BigDecimalUtils;
import com.lsh.payment.core.util.DateUtil;
import com.lsh.payment.core.util.PayAssert;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/3.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service
@Transactional(readOnly = true)
public class WxNotifyServiceImpl implements IWxNotifyService {

    private static Logger logger = LoggerFactory.getLogger(WxNotifyServiceImpl.class);

    @Autowired
    private PayDealService payDealService;

    @Autowired
    private PayBaseService payBaseService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void weChatNotify(Map<String, String> dataMap) {
        // return_code  值 SUCCESS/FAIL 此字段是通信标识，非交易标识
        PayAssert.notNull(dataMap.get("return_code"), ExceptionStatus.E2001006.getCode(), "return_code 参数异常");

        if(dataMap.get("return_code").equals(ExceptionStatus.FAIL_S.getMessage())){

            throw new BusinessException(ExceptionStatus.E2001006.getCode(),dataMap.get("return_msg"));
        }

        PayAssert.notNull(dataMap.get("result_code"), ExceptionStatus.E2001006.getCode(), "支付回调结果参数异常");

        String payPaymentNo = dataMap.get("out_trade_no");
        PayAssert.notNull(payPaymentNo, ExceptionStatus.E2001006.getCode(), ExceptionStatus.E2001006.getMessage());
        //支付平台订单信息 用于校验
        PayDeal payDeal = payDealService.queryPayDealByPayPaymentNo(payPaymentNo);

        if (payDeal == null || StringUtils.isBlank(payDeal.getPayPaymentNo())) {
            logger.info("微信第三方支付平台流水号对应支付数据不存在 ,payPaymentNo = [" + payPaymentNo + "]");
            throw new BusinessException(ExceptionStatus.E2001004.getCode(), ExceptionStatus.E2001004.getMessage());
        }

        //return_code 为 SUCCESS, 验证 result_code
        if (dataMap.get("result_code").equals(ExceptionStatus.SUCCESS_S.getMessage())) {

            //支付成功
            this.paySuccess(payDeal, dataMap);
        } else if (dataMap.get("result_code").equals(ExceptionStatus.FAIL_S.getMessage())) {

            //支付失败
            this.payFail(payDeal, dataMap);
        }

    }


    /**
     * 支付成功处理
     *
     * @param payDeal 支付平台订单信息
     * @param dataMap 参数map
     */
    private void paySuccess(PayDeal payDeal, Map<String, String> dataMap) {

        PayDeal pd = new PayDeal();
        pd.setPayId(payDeal.getPayId());

        //数据库支付记录已修改为支付成功
        if (payDeal.getPayStatus() == PayStatus.PAY_SUCCESS.getValue()) {

            logger.info("支付结果重复回调 : " + JSON.toJSONString(dataMap));

            return;
        }

        if (payDeal.getPayStatus() != PayStatus.PAY_SUCCESS.getValue()) {
            pd.setPayStatus(PayStatus.PAY_SUCCESS.getValue());
            pd.setOperateStatus(OperateStatus.PAYMENT_CALLBACK.getCode());

            PayAssert.notNull(dataMap.get("transaction_id"), ExceptionStatus.E2001004.getCode(), ExceptionStatus.E2001004.getMessage());
            PayAssert.notNull(dataMap.get("time_end"), ExceptionStatus.E2001009.getCode(), ExceptionStatus.E2001009.getMessage());

            logger.info("WX 第三方支付平台流水号 [" + dataMap.get("transaction_id") + "]");

            pd.setChannelTransaction(dataMap.get("transaction_id"));

            if (StringUtils.isNotBlank(dataMap.get("total_fee"))) {

                pd.setReceiveAmount(BigDecimalUtils.fenToYuan(dataMap.get("total_fee")));

                if (payDeal.getRequestAmount().compareTo(pd.getReceiveAmount()) != 0) {

                    logger.info("订单信息 : " + JSON.toJSONString(payDeal));
                    logger.info("订单金额与支付金额不相等 订单金额 : " + payDeal.getRequestAmount().toString() + "支付金额: " + pd.getReceiveAmount().toString());

                    throw new BusinessException(ExceptionStatus.E2001008.getCode(), ExceptionStatus.E2001008.getMessage());
                }

            }else{

                throw new BusinessException(ExceptionStatus.E2001008.getCode(), ExceptionStatus.E2001008.getMessage());
            }

            pd.setPayTime(DateUtil.parsePayDate(dataMap.get("time_end")));
            pd.setDoneTime(new Date());

            payBaseService.updPayDeal(pd, payDeal);

        }

    }

    /**
     * 支付失败处理
     *
     * @param payDeal 支付平台订单信息
     * @param dataMap 参数map
     */
    private void payFail(PayDeal payDeal, Map<String, String> dataMap) {

        logger.info(" lakla 支付回调 result_code is : " + dataMap.get("result_code"));
        logger.info(" lakla 支付回调 err_code is : " + dataMap.get("err_code"));
        logger.info(" lakla 支付回调 err_code_des is : " + dataMap.get("err_code_des"));

        PayDeal pd = new PayDeal();
        pd.setPayId(payDeal.getPayId());

        //数据库支付记录已修改为支付失败
        if (payDeal.getPayStatus() == PayStatus.PAY_FAIL.getValue()) {

            logger.info("支付结果重复回调 : " + JSON.toJSONString(dataMap));

            return;
        }

        if (payDeal.getPayStatus() != PayStatus.PAY_FAIL.getValue()) {
            pd.setPayStatus(PayStatus.PAY_FAIL.getValue());
            pd.setOperateStatus(OperateStatus.PAYMENT_CALLBACK.getCode());

            if (dataMap.get("transaction_id") != null) {

                pd.setChannelTransaction(dataMap.get("transaction_id"));
            }

            pd.setDoneTime(new Date());

            payBaseService.updPayDeal(pd, null);
        }
    }



}
