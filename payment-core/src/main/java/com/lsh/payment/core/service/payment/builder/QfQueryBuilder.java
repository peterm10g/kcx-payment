package com.lsh.payment.core.service.payment.builder;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.baseVo.QueryContent;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.model.payEnum.PayStatus;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.service.qfpay.QFPayHandleService;
import com.lsh.payment.core.strategy.config.QFPayConfig;
import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;
import com.lsh.payment.core.strategy.payVo.qfpay.QFData;
import com.lsh.payment.core.strategy.payVo.qfpay.QFQueryResponse;
import com.lsh.payment.core.util.BigDecimalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/27.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class QfQueryBuilder implements QueryBuilder {

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
            QFQueryResponse qfQueryResponse = null;
            if (basePayResponse instanceof QFQueryResponse) {
                qfQueryResponse = (QFQueryResponse) basePayResponse;
            }

            if (qfQueryResponse == null) {
                content.setPayCode(PayStatus.PAYING.getValue());
                content.setPayMsg(PayStatus.PAYING.getName());
                return;
            }

            logger.info("[qf query response is] : [" + JSON.toJSONString(qfQueryResponse) + "]");

            content.setTradeId(payDeal.getTradeId());
            content.setChannelTransaction(payDeal.getChannelTransaction());
            List<QFData> datas = qfQueryResponse.getData();

            if (datas != null && !datas.isEmpty() && datas.size() == 1) {
                QFData data = datas.get(0);

                //参数校验
                if (this.qfCheckParam(data, payDeal)) {

                    String dataRespcd = data.getRespcd();

                    if (dataRespcd.equals(QFPayConfig.RESPCD_SUCCESS)) {//请求成功
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

        QFQueryResponse qfQueryResponse = null;
        if (basePayResponse instanceof QFQueryResponse) {
            qfQueryResponse = (QFQueryResponse) basePayResponse;
        }
        if (qfQueryResponse == null) {
            return;
        }

        List<QFData> datas = qfQueryResponse.getData();
        QFData data = new QFData();
        if (datas != null && !datas.isEmpty() && datas.size() == 1) {
            data = datas.get(0);
        }

        if (content.getPayCode().equals(PayStatus.PAY_SUCCESS.getValue())) {

            updatePaydeal.setPayStatus(PayStatus.PAY_SUCCESS.getValue());
            updatePaydeal.setChannelTransaction(data.getSyssn());
            updatePaydeal.setReceiveAmount(BigDecimalUtils.fenToYuan(data.getTxamt()));
        } else if (content.getPayCode().equals(PayStatus.PAY_FAIL.getValue())) {

            updatePaydeal.setChannelTransaction(data.getSyssn());
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
     * @param data    返回数据
     * @param payDeal 支付记录
     * @return boolean
     */
    private boolean qfCheckParam(QFData data, PayDeal payDeal) {

        if (StringUtils.isBlank(data.getPay_type())) {
            //            ||
//        } !QFPayConfig.PAY_TYPE.equals(data.getPay_type())) {
            return false;
        }

        if (!QFPayHandleService.chenkParams(data.getPay_type(), payDeal.getTradeType())) {
            return false;
        }


        if (StringUtils.isBlank(data.getOrder_type()) || !QFPayConfig.ORDER_TYPE_PAYMENT.equals(data.getOrder_type())) {
            return false;
        }

        if (StringUtils.isBlank(data.getTxamt()) || !(payDeal.getRequestAmount().multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_DOWN).toString().equals(data.getTxamt()))) {

            return false;
        }

        if (StringUtils.isBlank(data.getOut_trade_no()) || !payDeal.getPayPaymentNo().equals(data.getOut_trade_no())) {

            return false;
        }

        if (StringUtils.isBlank(data.getRespcd())) {

            return false;
        }

        if (StringUtils.isBlank(data.getCancel()) || !QFPayConfig.CANCLE_NOMAL.equals(data.getCancel())) {

            return false;
        }

        if (StringUtils.isBlank(data.getTxcurrcd()) || !QFPayConfig.RMB.equals(data.getTxcurrcd())) {

            return false;
        }

        if (StringUtils.isBlank(data.getSyssn()) || !payDeal.getChannelTransaction().equals(data.getSyssn())) {

            return false;
        }

        return true;

    }
}
