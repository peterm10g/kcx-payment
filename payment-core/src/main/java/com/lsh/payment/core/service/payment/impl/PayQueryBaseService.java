package com.lsh.payment.core.service.payment.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.api.model.baseVo.QueryContent;
import com.lsh.payment.api.model.payment.PaymentQueryRequest;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.Async.QueryModel;
import com.lsh.payment.core.model.payEnum.PayChannel;
import com.lsh.payment.core.model.payEnum.PayStatus;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.service.AsyncService.AsyncEvent;
import com.lsh.payment.core.service.payment.IPayQueryChannelService;
import com.lsh.payment.core.util.PayAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/6.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service
@Transactional(readOnly = true)
public class PayQueryBaseService {

    private static Logger logger = LoggerFactory.getLogger(PayQueryBaseService.class);

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Autowired
    private Map<String, IPayQueryChannelService> payQueryChannelServices;

    @Autowired
    private PayDealService payDealService;


    @Transactional(rollbackFor = Exception.class)
    public QueryContent queryPayStatus(PaymentQueryRequest paymentQueryRequest) {

        QueryContent content = new QueryContent();

        PayDeal payDeal = null;
        //支付平台流水号有值
        if (StringUtils.isNotEmpty(paymentQueryRequest.getPay_payment_no())) {
            payDeal = payDealService.queryPayDealByPayPaymentNo(paymentQueryRequest.getPay_payment_no());

            PayAssert.notNull(payDeal, ExceptionStatus.E2001012.getCode(), ExceptionStatus.E2001012.getMessage() + " payPaymentNo is : " + paymentQueryRequest.getPay_payment_no());
        } else if (StringUtils.isNotEmpty(paymentQueryRequest.getChannel_transaction())) {
            payDeal = payDealService.queryPayDealByChannelTransaction(paymentQueryRequest.getChannel_transaction());

            PayAssert.notNull(payDeal, ExceptionStatus.E2001012.getCode(), ExceptionStatus.E2001012.getMessage() + " channel_transaction is : " + paymentQueryRequest.getChannel_transaction());

        } else if (StringUtils.isNotEmpty(paymentQueryRequest.getTrade_id())) {
            List<PayDeal> payList = payDealService.queryPayDealByTradeId(paymentQueryRequest.getTrade_id());
            //交易记录不存在
            PayAssert.notEmpty(payList, ExceptionStatus.E2001012.getCode(), ExceptionStatus.E2001012.getMessage() + " orderId is : " + paymentQueryRequest.getTrade_id());

            //1 有一条支付记录,支付成功,返回成功
            for (PayDeal pay : payList) {
                if (pay.getPayStatus() == PayStatus.PAY_SUCCESS.getValue()) {
                    //已经支付成功,直接返回
                    payDeal = pay;
                    break;
                } else if (pay.getPayStatus() == PayStatus.PAYING.getValue() || pay.getPayStatus() == PayStatus.CREATE_PAYMENT.getValue()) {
                    //未支付,后续处理
                    payDeal = pay;
                }
            }

            //多条记录时,没有支付成功,也没有支付中,则支付失败
            if (payDeal == null) {
                content.setPayCode(PayStatus.PAY_FAIL.getValue());
                content.setPayMsg(PayStatus.PAY_FAIL.getName());

                return content;
            }

        } else {

            throw new BusinessException(ExceptionStatus.E1002001.getCode(), "查询支付记录参数异常");
        }

        return parsePayDeal(payDeal);
    }

    /**
     * 解析支付记录状态
     *
     * @param payDeal
     * @return
     */
    private QueryContent parsePayDeal(PayDeal payDeal) {

        QueryContent content = new QueryContent();

        if (payDeal != null) {
            this.setSpeVar(content, payDeal);
        }
        //(1)有一条支付记录,支付成功,返回成功
        if (payDeal != null && payDeal.getPayStatus() != null && payDeal.getPayStatus() == PayStatus.PAY_SUCCESS.getValue()) {
            //已经支付成功,直接返回
            content.setPayCode(PayStatus.PAY_SUCCESS.getValue());
            content.setPayMsg(PayStatus.PAY_SUCCESS.getName());

            return content;
        }

        //(2)没有支付成功和未支付状态的,支付失败
        if (isPayFail(payDeal)) {

            content.setPayCode(PayStatus.PAY_FAIL.getValue());
            content.setPayMsg(PayStatus.PAY_FAIL.getName());

            return content;
        }

        //(3)未支付,调第三方进行查询
        if (payDeal.getPayChannel().equals(PayChannel.WXPAY.getName())) {

            return payQueryChannelServices.get(BusiConstant.QUERY_WX_SERVICE_NAME).query(payDeal);
        }

        if (payDeal.getPayChannel().equals(PayChannel.QFPAY.getName())) {
            //钱方异步处理
//            return payQueryChannelServices.get(BusiConstant.QUERY_QF_SERVICE_NAME).query(payDeal);
            AsyncEvent.post(new QueryModel(payDeal));
            content.setPayCode(PayStatus.PAYING.getValue());
            content.setPayMsg(PayStatus.PAYING.getName());
        }

        if (payDeal.getPayChannel().equals(PayChannel.XYPAY.getName())) {
            //兴业
            return payQueryChannelServices.get(BusiConstant.QUERY_XY_SERVICE_NAME).query(payDeal);
        }

        if (payDeal.getPayChannel().equals(PayChannel.CMBCPAY.getName())) {
            //民生
            return payQueryChannelServices.get(BusiConstant.QUERY_CMBC_SERVICE_NAME).query(payDeal);
        }

        if (payDeal.getPayChannel().equals(PayChannel.ALLINPAY.getName())) {
            //通联
            return payQueryChannelServices.get(BusiConstant.QUERY_ALLIN_SERVICE_NAME).query(payDeal);
        }

        //未支付,调第三方进行查询
        if (payDeal.getPayChannel().equals(PayChannel.ALIPAY.getName())) {

            return payQueryChannelServices.get(BusiConstant.QUERY_ALI_SERVICE_NAME).query(payDeal);
        }

        return content;
    }

    private void setSpeVar(QueryContent content, PayDeal payDeal) {

        content.setTradeId(payDeal.getTradeId());
        content.setChannelTransaction(payDeal.getChannelTransaction());
    }

    /**
     * 判断是否支付失败,没有支付成功,也没有支付中,
     *
     * @param payDeal
     */
    private boolean isPayFail(PayDeal payDeal) {
        if (payDeal == null) {
            return false;
        }

        //支付成功
        if (payDeal.getPayStatus() == PayStatus.PAY_SUCCESS.getValue()) {
            return false;
        }

        //支付中
        if (payDeal.getPayStatus() == PayStatus.PAYING.getValue() || payDeal.getPayStatus() == PayStatus.CREATE_PAYMENT.getValue()) {
            return false;
        }
        //单方面关单
        if (payDeal.getPayStatus() == PayStatus.PAY_CLOSE.getValue() ) {
            return false;
        }

        //支付失败
        return true;
    }


}
