package com.lsh.payment.core.service.businessNotify;

import com.alibaba.fastjson.JSON;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.dao.payNotifyTmsTask.PayNotifyTmsTaskDao;
import com.lsh.payment.core.model.payEnum.PayChannel;
import com.lsh.payment.core.model.payEnum.TradeType;
import com.lsh.payment.core.model.payNotifyTmsTask.PayNotifyTmsTask;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.service.payment.impl.PayDealService;
import com.lsh.payment.core.util.HttpClientUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/11/7
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.service.notifyTms
 * desc:
 */
@Service
public class BusinessNotifyService {

    private static Logger logger = LoggerFactory.getLogger(BusinessNotifyService.class);

    private static Map<Integer,Integer> strategyMap;

    @Autowired
    private PayNotifyTmsTaskDao payNotifyTmsTaskDao;

    @Autowired
    private PayDealService payDealService;


    static {
        strategyMap = new HashMap<>();
        strategyMap.put(TradeType.XYWXSM.getCode(),TradeType.QFWXSM.getCode());
        strategyMap.put(TradeType.XYALISM.getCode(),TradeType.QFALISM.getCode());
        strategyMap.put(TradeType.CMBCWXSM.getCode(),TradeType.QFWXSM.getCode());
        strategyMap.put(TradeType.CMBCALISM.getCode(),TradeType.QFALISM.getCode());
        strategyMap.put(TradeType.ALLINWXSM.getCode(),TradeType.QFWXSM.getCode());
        strategyMap.put(TradeType.ALLINALISM.getCode(),TradeType.QFALISM.getCode());
    }

    /**
     * 定时任务调用(通知支付结果)
     *
     * @param payNotifyTmsTask 定时任务项
     * @return int
     */
    public int dealNotify(PayNotifyTmsTask payNotifyTmsTask) {

        if (payNotifyTmsTask == null) {
            return 0;
        }

        payNotifyTmsTask.setNotifyTimes(payNotifyTmsTask.getNotifyTimes() + 1);

        PayDeal payDeal = payDealService.queryPayDealByPayPaymentNo(payNotifyTmsTask.getPayPaymentNo());
        if (payDeal != null) {
            payNotifyTmsTask.setTraceModule(payDeal.getTradeModule());
        }

        if (this.sendNotify(payNotifyTmsTask)) {
            payNotifyTmsTask.setStatus(BusiConstant.TMS_TASK_STATUS_SUCCESS);
        } else {
            if (payNotifyTmsTask.getNotifyTimes() >= BusiConstant.TMS_MAX_TIMES && payNotifyTmsTask.getStatus().equals(BusiConstant.TMS_TASK_STATUS_NEW)) {
                payNotifyTmsTask.setStatus(BusiConstant.TMS_TASK_STATUS_FAIL);
            }
        }

        return payNotifyTmsTaskDao.updateByPrimaryKeySelective(payNotifyTmsTask);
    }

    /**
     * 同步调用(通知支付结果)
     *
     * @param payDeal 支付记录
     */
    public void dealNotify(PayDeal payDeal) {

        String prefix = payDeal.getPayPaymentNo() + "-【回调业务方】";
        logger.info("{} start",prefix);
        Integer method;
        switch (payDeal.getPayChannel()) {

            case "alipay":
                method = PayChannel.ALIPAY.getCode();
                break;
            case "wxpay":
                method = PayChannel.WXPAY.getCode();
                break;
            case "lklpay":
                method = PayChannel.LKLPAY.getCode();
                break;
            case "qfpay":
                method = payDeal.getTradeType();
                break;
            case "xypay":
                method = payDeal.getTradeType();
                break;
            case "cmbcpay":
                method = payDeal.getTradeType();
                break;
            case "allinpay":
                method = payDeal.getTradeType();
                break;
            default:
                method = 0;
        }
        logger.info("{}  method is {}",prefix,method);
        if (method == 0) {
            return;
        }

        PayNotifyTmsTask payNotifyTmsTask = new PayNotifyTmsTask();
        payNotifyTmsTask.setTradeId(payDeal.getTradeId());
        payNotifyTmsTask.setMethod(method);
        payNotifyTmsTask.setNotifyTimes(1);
        payNotifyTmsTask.setReceiveAmount(payDeal.getRequestAmount());
        payNotifyTmsTask.setLshNotifyUrl(payDeal.getLshNotifyUrl());
        payNotifyTmsTask.setPayPaymentNo(payDeal.getPayPaymentNo());
        payNotifyTmsTask.setTraceModule(payDeal.getTradeModule());

        long t1 = System.currentTimeMillis();
        logger.info("{}  payNotifyTmsTask is {}",prefix,JSON.toJSONString(payNotifyTmsTask));
        if (!this.sendNotify(payNotifyTmsTask)) {
            payNotifyTmsTaskDao.insertSelective(payNotifyTmsTask);
        }
        long t2 = System.currentTimeMillis();
        logger.info("真实通知业务方支付结果时间 [" + (t2 - t1) + "] 毫秒, NotifyUrl is [" + payNotifyTmsTask.getLshNotifyUrl() + "]");
    }


    /**
     * 发送通知
     *
     * @param payNotifyTmsTask 通知业务方
     * @return boolean         返回结果
     */
    private boolean sendNotify(PayNotifyTmsTask payNotifyTmsTask) {
        String prefix = payNotifyTmsTask.getPayPaymentNo() + "-【回调业务方】[sendNotify] ";
        logger.info("{} start",prefix);

        boolean notifySuccessFlag = false;
        Map<String, Object> notifyRequestMap = new HashMap<>(8);
        notifyRequestMap.put("trade_id", payNotifyTmsTask.getTradeId());
        int type = payNotifyTmsTask.getMethod();
        // TODO 需优化
        if(type >= TradeType.XYWXSM.getCode()){
            type = strategyMap.get(type);
        }
        logger.info("{} pay type is {}",prefix,type);
        PayDeal payDeal = payDealService.queryPayDealByPayPaymentNo(payNotifyTmsTask.getPayPaymentNo());

        if(payDeal != null){
            //支付渠道
            PayChannel payChannel = PayChannel.getPayChannel(payDeal.getPayChannel());
            logger.info("{} pay payChannel is {}",prefix,payChannel);
            if(payChannel != null){
                notifyRequestMap.put("pay_channel", payChannel.getCode());
            }
        }

        logger.info("{} pay notifyRequestMap is {}",prefix,JSON.toJSONString(notifyRequestMap));
        //老字段
        notifyRequestMap.put("channel_type", type);
        //支付类型
//        notifyRequestMap.put("pay_type", type);

        notifyRequestMap.put("payment_amount", payNotifyTmsTask.getReceiveAmount());
        notifyRequestMap.put("pay_payment_no", payNotifyTmsTask.getPayPaymentNo());
        notifyRequestMap.put("pay_code", "SUCCESS");
        notifyRequestMap.put("trade_module", payNotifyTmsTask.getTraceModule());
        try {
            String notifyRequestJson = JSON.toJSONString(notifyRequestMap);
            logger.info("JOP 通知业务方 : " + notifyRequestJson);
            String notifyResponse = HttpClientUtils.doPostBody(payNotifyTmsTask.getLshNotifyUrl(), notifyRequestJson);

            logger.info("asyn notify response is : [" + notifyResponse + "]");
            String returnCode = null;
            if (StringUtils.isNotBlank(notifyResponse)) {
                JSONObject notifyResponseJson = JSONObject.fromObject(notifyResponse);
                if (notifyResponseJson != null && notifyResponseJson.getJSONObject("content") != null) {
                    returnCode = notifyResponseJson.getJSONObject("content").getString("return_code");
                }
            }

            if (StringUtils.isNotBlank(returnCode) && returnCode.equals("SUCCESS")) {
                notifySuccessFlag = true;
            } else {
                logger.info("asyn notify fail : tradeId is : " + payNotifyTmsTask.getTradeId() + " ,return_msg is : " + notifyResponse);
            }
        } catch (Exception e) {

            logger.error("通知业务方失败:" + payNotifyTmsTask.getTradeId(), e);
        }

        return notifySuccessFlag;
    }

}
