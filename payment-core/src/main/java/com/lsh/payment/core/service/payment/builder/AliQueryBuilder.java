package com.lsh.payment.core.service.payment.builder;

import com.alibaba.fastjson.JSON;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.lsh.payment.api.model.baseVo.QueryContent;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.model.payEnum.PayStatus;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.strategy.config.AliPayConfig;
import com.lsh.payment.core.strategy.payVo.alipay.AliQueryResponse;
import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/27.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class AliQueryBuilder implements QueryBuilder {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private QueryContent content = new QueryContent();

    private PayDeal updatePaydeal = new PayDeal();

    @Override
    public void parse(BasePayResponse basePayResponse, PayDeal payDeal) {

        content.setTradeId(payDeal.getTradeId());
        content.setChannelTransaction(payDeal.getChannelTransaction());

        logger.info("[ali base query response is] : [" + JSON.toJSONString(basePayResponse) + "]");

        if (basePayResponse == null || basePayResponse.getCode() == null || basePayResponse.getCode().equals(BusiConstant.PAY_REQUEST_FAIL)) {
            content.setPayCode(PayStatus.PAYING.getValue());
            content.setPayMsg(PayStatus.PAYING.getName());

        } else {
            AliQueryResponse aliQueryResponse = null;
            if(basePayResponse instanceof AliQueryResponse){
                aliQueryResponse = (AliQueryResponse) basePayResponse;
            }

            if(aliQueryResponse == null){
                //交易失败,更新
                content.setPayCode(PayStatus.PAYING.getValue());
                content.setPayMsg(PayStatus.PAYING.getName());
                return ;
            }

//            logger.info("[ali query response is] : [" + JSON.toJSONString(aliQueryResponse) + "]");

            if (this.checkParams(aliQueryResponse, payDeal)) {
                //支付宝查询后处理
                //交易状态：WAIT_BUYER_PAY（交易创建，等待买家付款）、TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）、TRADE_SUCCESS（交易支付成功）、TRADE_FINISHED（交易结束，不可退款）
                String tradeStatus = aliQueryResponse.getResp().getTradeStatus();
                if (tradeStatus.equals(AliPayConfig.TRADE_SUCCESS) || tradeStatus.equals(AliPayConfig.TRADE_FINISHED)) {
                    //交易成功
                    content.setPayCode(PayStatus.PAY_SUCCESS.getValue());
                    content.setPayMsg(PayStatus.PAY_SUCCESS.getName());

                } else if (tradeStatus.equals(AliPayConfig.TRADE_CLOSED)) {//支付失败
                    //交易失败,更新
                    content.setPayCode(PayStatus.PAY_FAIL.getValue());
                    content.setPayMsg(PayStatus.PAY_FAIL.getName());
                } else {//支付中
                    //查询次数加一
                    content.setPayCode(PayStatus.PAYING.getValue());
                    content.setPayMsg(PayStatus.PAYING.getName());
                }
            }else{
                logger.info("class AliQueryBuilder alipay paydeal is [" + payDeal.getPayPaymentNo() + "], 参数检查错误。");
            }
        }
    }

    @Override
    public void createUpdatePaydeal(BasePayResponse basePayResponse, PayDeal payDeal) {

        updatePaydeal.setPayId(payDeal.getPayId());
        updatePaydeal.setQueryTime(new Date());
        updatePaydeal.setDoneTime(new Date());

        if (content == null) {
            return;
        }

        AliQueryResponse aliQueryResponse = null;
        if(basePayResponse instanceof AliQueryResponse){
            aliQueryResponse = (AliQueryResponse) basePayResponse;
        }

        if(aliQueryResponse == null){
            return ;
        }

        if (content.getPayCode().equals(PayStatus.PAY_SUCCESS.getValue())) {
            updatePaydeal.setReceiveAmount(new BigDecimal(aliQueryResponse.getResp().getTotalAmount()));
            updatePaydeal.setPayStatus(PayStatus.PAY_SUCCESS.getValue());
            updatePaydeal.setChannelTransaction(aliQueryResponse.getResp().getTradeNo());
        } else if (content.getPayCode().equals(PayStatus.PAY_FAIL.getValue())) {
            updatePaydeal.setChannelTransaction(aliQueryResponse.getResp().getTradeNo());
            updatePaydeal.setPayStatus(PayStatus.PAY_FAIL.getValue());
        }
    }

    private boolean checkParams(AliQueryResponse aliQueryResponse, PayDeal payDeal) {
        AlipayTradeQueryResponse alipayTradeQueryResponse = aliQueryResponse.getResp();

        if (StringUtils.isBlank(alipayTradeQueryResponse.getTotalAmount()) && (new BigDecimal(alipayTradeQueryResponse.getTotalAmount()).compareTo(payDeal.getRequestAmount())) != 0) {
            logger.info("alipay checkParams paydeal is [" + payDeal.getPayPaymentNo() + "],TotalAmount is [" + alipayTradeQueryResponse.getTotalAmount() + "]");
            return false;
        }

        if (StringUtils.isBlank(alipayTradeQueryResponse.getTradeNo())) {
            logger.info("alipay checkParams paydeal is [" + payDeal.getPayPaymentNo() + "],TradeNo is [" + alipayTradeQueryResponse.getTradeNo() + "]");
            return false;
        }

        if (StringUtils.isBlank(alipayTradeQueryResponse.getOutTradeNo()) && !alipayTradeQueryResponse.getOutTradeNo().equals(payDeal.getPayPaymentNo())) {
            logger.info("alipay checkParams paydeal is [" + payDeal.getPayPaymentNo() + "],OutTradeNo is [" + alipayTradeQueryResponse.getOutTradeNo() + "]");
            return false;
        }

        return true;
    }

    /**
     * 拿到解析后的结果
     *
     * @return
     */
    public QueryContent getResult() {
        return content;
    }

    /**
     * 拿到解析后的结果
     *
     * @return
     */
    public PayDeal getUpdatePaydeal() {
        return updatePaydeal;
    }
}
