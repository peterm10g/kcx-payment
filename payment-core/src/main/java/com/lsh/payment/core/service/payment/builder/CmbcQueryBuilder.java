package com.lsh.payment.core.service.payment.builder;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.baseVo.QueryContent;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.model.payEnum.PayStatus;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;
import com.lsh.payment.core.strategy.payVo.cmbcpay.CmbcQueryResponse;
import com.lsh.payment.core.util.BigDecimalUtils;
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
public class CmbcQueryBuilder implements QueryBuilder {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private QueryContent content = new QueryContent();

    private PayDeal updatePaydeal = new PayDeal();

    @Override
    public void parse(BasePayResponse basePayResponse, PayDeal payDeal) {

        content.setTradeId(payDeal.getTradeId());
        content.setChannelTransaction(payDeal.getChannelTransaction());

        logger.info("[qf base query response is] : [" + JSON.toJSONString(basePayResponse) + "]");

        if (basePayResponse == null || StringUtils.isNotEmpty(basePayResponse.getCode()) || basePayResponse.getCode().equals(BusiConstant.PAY_REQUEST_FAIL)) {
            content.setPayCode(PayStatus.PAYING.getValue());
            content.setPayMsg(PayStatus.PAYING.getName());

        } else {
            CmbcQueryResponse cmbcQueryResponse = null;
            if (basePayResponse instanceof CmbcQueryResponse) {
                cmbcQueryResponse = (CmbcQueryResponse) basePayResponse;
            }

            if (cmbcQueryResponse == null) {
                content.setPayCode(PayStatus.PAYING.getValue());
                content.setPayMsg(PayStatus.PAYING.getName());
                return;
            }

            logger.info("[cmbc query response is] : [" + JSON.toJSONString(cmbcQueryResponse) + "]");

            content.setTradeId(payDeal.getTradeId());
            content.setChannelTransaction(payDeal.getChannelTransaction());

            //参数校验
            if (this.cmbcCheckParam(cmbcQueryResponse, payDeal)) {

                String dataRespcd = cmbcQueryResponse.getCode();

                if (dataRespcd.equals(BusiConstant.PAY_REQUEST_SUCCESS)) {//请求成功
                    //交易成功
                    content.setPayCode(PayStatus.PAY_SUCCESS.getValue());
                    content.setPayMsg(PayStatus.PAY_SUCCESS.getName());

                } else {
                    //鉴于钱方的返回码不明确,非成功的都当支付中来看
                    content.setPayCode(PayStatus.PAYING.getValue());
                    content.setPayMsg(PayStatus.PAYING.getName());
                }
            }else{
                //鉴于钱方的返回码不明确,非成功的都当支付中来看
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

        CmbcQueryResponse cmbcQueryResponse = null;
        if (basePayResponse instanceof CmbcQueryResponse) {
            cmbcQueryResponse = (CmbcQueryResponse) basePayResponse;
        }

        if (cmbcQueryResponse == null) {
            return;
        }

        if (content.getPayCode().equals(PayStatus.PAY_SUCCESS.getValue())) {

            updatePaydeal.setPayStatus(PayStatus.PAY_SUCCESS.getValue());
            updatePaydeal.setChannelTransaction(cmbcQueryResponse.getOrgTransId());
            updatePaydeal.setReceiveAmount(BigDecimalUtils.fenToYuan(cmbcQueryResponse.getAmount()));
        } else if (content.getPayCode().equals(PayStatus.PAY_FAIL.getValue())) {

            updatePaydeal.setChannelTransaction(cmbcQueryResponse.getOrgTransId());
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
     * @param cmbcQueryResponse    返回数据
     * @param payDeal 支付记录
     * @return boolean
     */
    private boolean cmbcCheckParam(CmbcQueryResponse cmbcQueryResponse, PayDeal payDeal) {

        if (StringUtils.isBlank(cmbcQueryResponse.getResult())) {
            return false;
        }

        if (StringUtils.isBlank(cmbcQueryResponse.getAmount()) || !(payDeal.getRequestAmount().multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_DOWN).toString().equals(cmbcQueryResponse.getAmount()))) {
            return false;
        }

        if (StringUtils.isBlank(cmbcQueryResponse.getOrderNo()) || !payDeal.getPayPaymentNo().equals(cmbcQueryResponse.getOrderNo())) {
            return false;
        }

        if (StringUtils.isBlank(cmbcQueryResponse.getOrgTransId()) || !payDeal.getChannelTransaction().equals(cmbcQueryResponse.getOrgTransId())) {
            return false;
        }

        return true;

    }
}
