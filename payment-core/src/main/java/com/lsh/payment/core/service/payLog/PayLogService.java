package com.lsh.payment.core.service.payLog;

import com.lsh.payment.core.model.payLog.PayLog;
import com.lsh.payment.core.service.AsyncService.AsyncEvent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Project Name: lsh-atp
 * Created by miaozhuang
 * Date: 16/7/16
 * 北京链商电子商务有限公司
 * Package com.lsh.atp.core.service.hold.
 * desc:预占操作日志
 */
@Component
public class PayLogService {

    private static Logger logger = LoggerFactory.getLogger(PayLogService.class);

    /**
     * @param payId        支付
     * @param payPaymentNo
     * @param payType
     * @param status
     * @param params
     * @param backresult
     */
    public void insertLog(String payId, String payPaymentNo, String tradeId, int payType, int status, String params, String backresult) {
        try {

            int currentTime = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

            PayLog payLog = new PayLog();
            payLog.setPayId(payId);
            payLog.setPayPaymentNo(payPaymentNo);
            payLog.setTradeId(tradeId);
            payLog.setPayType(payType);
            payLog.setStatus(status);
            payLog.setCreatedAt(new Date());
            payLog.setUpdatedAt(new Date());
            payLog.setOperateTime(currentTime);

            if (StringUtils.isNotBlank(params) && params.length() > 1480) {
                payLog.setParams(params.substring(0, 1450));
                logger.info("payment params is : " + params);

            } else {
                payLog.setParams(params);
            }

            if (StringUtils.isNotBlank(backresult) && backresult.length() > 1480) {
                payLog.setBackresult(backresult.substring(0, 1450));
                logger.info("payment backresult is : " + backresult);
            } else {
                payLog.setBackresult(backresult);
            }

            //异步处理
            AsyncEvent.post(payLog);

        } catch (Exception e) {

            logger.error("记录日志异常", e);
        }

    }


}
