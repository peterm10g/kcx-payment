package com.lsh.payment.core.service.payment.builder;

import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.baseVo.QueryContent;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.model.payEnum.PayStatus;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.strategy.config.WxPayConfig;
import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;
import com.lsh.payment.core.strategy.payVo.wxpay.WxQueryResponse;
import com.lsh.payment.core.util.BigDecimalUtils;
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
public class WxQueryBuilder implements QueryBuilder {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private QueryContent content = new QueryContent();

    private PayDeal updatePaydeal = new PayDeal();

    @Override
    public void parse(BasePayResponse basePayResponse, PayDeal payDeal) {

        content.setTradeId(payDeal.getTradeId());
        content.setChannelTransaction(payDeal.getChannelTransaction());

        logger.info("[wx base query response is] : [" + JSON.toJSONString(basePayResponse) + "]");

        if (basePayResponse == null || basePayResponse.getCode() == null || basePayResponse.getCode().equals(BusiConstant.PAY_REQUEST_FAIL)) {
            content.setPayCode(PayStatus.PAYING.getValue());
            content.setPayMsg(PayStatus.PAYING.getName());

        } else {

            //支付签名成功
            WxQueryResponse wxQueryResponse = null;

            if (basePayResponse instanceof WxQueryResponse) {
                wxQueryResponse = (WxQueryResponse) basePayResponse;
            }

            if (wxQueryResponse == null) {
                content.setPayCode(PayStatus.PAYING.getValue());
                content.setPayMsg(PayStatus.PAYING.getName());
                return;
            }

            logger.info("[wx query response is] : [" + JSON.toJSONString(wxQueryResponse) + "]");


            //以下字段在return_code 和result_code都为SUCCESS的时候有返回
            //SUCCESS—支付成功 ,REFUND—转入退款 ,NOTPAY—未支付 ,CLOSED—已关闭
            //REVOKED—已撤销（刷卡支付）,USERPAYING--用户支付中 ,PAYERROR--支付失败(其他原因，如银行返回失败)
            String trateStatus = wxQueryResponse.getTrade_state();

            if (trateStatus.equals(WxPayConfig.WX_SUCCESS)) {
                if (!this.checkParams(wxQueryResponse, payDeal)){
                    content.setPayCode(PayStatus.PAYING.getValue());
                    content.setPayMsg(PayStatus.PAYING.getName());
                    return;
                }
                content.setPayCode(PayStatus.PAY_SUCCESS.getValue());
                content.setPayMsg(PayStatus.PAY_SUCCESS.getName());
//                content.put("trade_state", wxQueryResponse.getTrade_state());

            } else if (trateStatus.equals(WxPayConfig.WX_CLOSED) || trateStatus.equals(WxPayConfig.WX_PAYERROR)) {

                logger.info("查询支付状态 : trade_state = " + wxQueryResponse.getTrade_state());
//                content.put("trade_state", wxQueryResponse.getTrade_state());
                content.setPayCode(PayStatus.PAY_FAIL.getValue());
                content.setPayMsg(PayStatus.PAY_FAIL.getName());

            } else if (trateStatus.equals(WxPayConfig.WX_NOTPAY) || trateStatus.equals(WxPayConfig.WX_USERPAYING)) {

                //支付未完成
                logger.info("查询支付状态 : trade_state = " + wxQueryResponse.getTrade_state());

//                content.put("trade_state", wxQueryResponse.getTrade_state());
                content.setPayCode(PayStatus.PAYING.getValue());
                content.setPayMsg(PayStatus.PAYING.getName());
            } else {
                //支付异常
                logger.info("查询支付状态 : trade_state = " + wxQueryResponse.getTrade_state());
            }

        }

    }

    @Override
    public void createUpdatePaydeal(BasePayResponse basePayResponse, final PayDeal payDeal) {

        updatePaydeal.setPayId(payDeal.getPayId());
        updatePaydeal.setQueryTime(new Date());

        if (content == null) {
            return;
        }

        WxQueryResponse wxQueryResponse = null;

        if (basePayResponse instanceof WxQueryResponse) {
            wxQueryResponse = (WxQueryResponse) basePayResponse;
        }

        if (wxQueryResponse == null) {

            return;
        }

        if (content.getPayCode().equals(PayStatus.PAY_SUCCESS.getValue())) {

            updatePaydeal.setPayStatus(PayStatus.PAY_SUCCESS.getValue());
            updatePaydeal.setChannelTransaction(wxQueryResponse.getTransaction_id());
            updatePaydeal.setReceiveAmount(BigDecimalUtils.fenToYuan(wxQueryResponse.getTotal_fee()));
        } else if (content.getPayCode().equals(PayStatus.PAY_FAIL.getValue())) {
            updatePaydeal.setChannelTransaction(wxQueryResponse.getTransaction_id());
            updatePaydeal.setPayStatus(PayStatus.PAY_FAIL.getValue());
        }

    }

    private boolean checkParams(WxQueryResponse wxQueryResponse, PayDeal payDeal) {

        if (StringUtils.isBlank(wxQueryResponse.getTotal_fee()) && (new BigDecimal(wxQueryResponse.getTotal_fee()).compareTo(payDeal.getRequestAmount())) != 0) {
            return false;
        }

        if (StringUtils.isBlank(wxQueryResponse.getTransaction_id())) {
            return false;
        }

        if (StringUtils.isBlank(wxQueryResponse.getOut_trade_no()) && !wxQueryResponse.getOut_trade_no().equals(payDeal.getPayPaymentNo())) {
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
