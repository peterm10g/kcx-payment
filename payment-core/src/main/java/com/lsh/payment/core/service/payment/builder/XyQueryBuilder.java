package com.lsh.payment.core.service.payment.builder;

import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.baseVo.QueryContent;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.model.payEnum.PayStatus;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.strategy.config.XYPayConfig;
import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;
import com.lsh.payment.core.strategy.payVo.xypay.XyPayOrderQueryResult;
import com.lsh.payment.core.strategy.payVo.xypay.XyQueryResponse;
import com.lsh.payment.core.util.BigDecimalUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/27.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class XyQueryBuilder implements QueryBuilder {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private QueryContent content = new QueryContent();

    private PayDeal updatePaydeal = new PayDeal();

    @Override
    public void parse(BasePayResponse basePayResponse, PayDeal payDeal) {

        content.setTradeId(payDeal.getTradeId());
        content.setChannelTransaction(payDeal.getChannelTransaction());

        logger.info("[qf base query response is] : [" + JSON.toJSONString(basePayResponse) + "]");

        if (basePayResponse == null || basePayResponse.getCode() == null || basePayResponse.getCode().equals(BusiConstant.PAY_REQUEST_FAIL)) {
            content.setPayCode(PayStatus.PAYING.getValue());
            content.setPayMsg(PayStatus.PAYING.getName());

        } else {
            XyQueryResponse xyQueryResponse = null;
            if (basePayResponse instanceof XyQueryResponse) {
                xyQueryResponse = (XyQueryResponse) basePayResponse;
            }

            if (xyQueryResponse == null) {
                content.setPayCode(PayStatus.PAYING.getValue());
                content.setPayMsg(PayStatus.PAYING.getName());
                return;
            }

            XyPayOrderQueryResult xyPayOrderQueryResult = xyQueryResponse.getXyPayOrderQueryResult();

            logger.info("[qf query response is] : [" + JSON.toJSONString(xyQueryResponse) + "]");

            content.setTradeId(payDeal.getTradeId());

            if (xyPayOrderQueryResult != null ) {
                content.setChannelTransaction(xyPayOrderQueryResult.getTransactionId());

                //参数校验
                if (this.xyCheckParam(xyPayOrderQueryResult, payDeal)) {

                    String tradeState = xyPayOrderQueryResult.getTradeState();

                    if (tradeState.equals(XYPayConfig.TRADE_STATE_SUCCESS)) {//请求成功
                        //交易成功
                        content.setPayCode(PayStatus.PAY_SUCCESS.getValue());
                        content.setPayMsg(PayStatus.PAY_SUCCESS.getName());

                    } else {
                        //鉴于钱方的返回码不明确,非成功的都当支付中来看
                        content.setPayCode(PayStatus.PAYING.getValue());
                        content.setPayMsg(PayStatus.PAYING.getName());
                    }
                }
            } else {
                //data为空的时候,支付中
                content.setPayCode(PayStatus.PAYING.getValue());
                content.setPayMsg(PayStatus.PAYING.getName());
            }
        }

    }

    @Override
    public void createUpdatePaydeal(BasePayResponse basePayResponse, PayDeal payDeal) {

        updatePaydeal.setPayId(payDeal.getPayId());
        updatePaydeal.setQueryTime(new Date());
        updatePaydeal.setDoneTime(new Date());

        if (content == null || content.getPayCode() == null) {
            return;
        }

        XyQueryResponse xyQueryResponse = null;
        if (basePayResponse instanceof XyQueryResponse) {
            xyQueryResponse = (XyQueryResponse) basePayResponse;
        }
        if (xyQueryResponse == null) {
            return;
        }

        XyPayOrderQueryResult xyPayOrderQueryResult = xyQueryResponse.getXyPayOrderQueryResult();

        if (xyPayOrderQueryResult == null) {
            return;
        }

        if (content.getPayCode().equals(PayStatus.PAY_SUCCESS.getValue())) {

            updatePaydeal.setPayStatus(PayStatus.PAY_SUCCESS.getValue());
            updatePaydeal.setChannelTransaction(xyPayOrderQueryResult.getTransactionId());
            updatePaydeal.setReceiveAmount(BigDecimalUtils.fenToYuan(xyPayOrderQueryResult.getTotalFee()+""));
        } else if (content.getPayCode().equals(PayStatus.PAY_FAIL.getValue())) {

            updatePaydeal.setChannelTransaction(xyPayOrderQueryResult.getTransactionId());
            updatePaydeal.setPayStatus(PayStatus.PAY_FAIL.getValue());
        }

    }

    /**
     * 拿到解析后的结果
     *
     * @return
     */
    public QueryContent getResult() {

        logger.info("qf parse content is [" + JSON.toJSONString(content) + "]");

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

    /**
     * 校验参数
     *
     * @param xyPayOrderQueryResult    返回数据
     * @param payDeal 支付记录
     * @return boolean
     */
    private boolean xyCheckParam(XyPayOrderQueryResult xyPayOrderQueryResult, PayDeal payDeal) {

        if (StringUtils.isBlank(xyPayOrderQueryResult.getStatus())) {
            return false;
        }

//        if (StringUtils.isBlank(xyPayOrderQueryResult.getOutTradeNo())) {
//            return false;
//        }

        if (StringUtils.isBlank(xyPayOrderQueryResult.getTradeState())) {
            return false;
        }

        if (StringUtils.isBlank(xyPayOrderQueryResult.getResultCode())) {
            return false;
        }

        return true;

    }
}
