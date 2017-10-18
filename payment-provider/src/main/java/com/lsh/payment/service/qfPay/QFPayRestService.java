package com.lsh.payment.service.qfPay;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;
import com.lsh.payment.api.service.qfPay.IQFPayRestService;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.Async.PayMonitorInterfaceModel;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.service.AsyncService.AsyncEvent;
import com.lsh.payment.core.service.payLog.PayLogService;
import com.lsh.payment.core.service.qfpay.QFPayHandleService;
import com.lsh.payment.core.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/10/31
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.service.qfPay
 * desc:
 */
@Service(protocol = "rest", validation = "true")
@Path("qrcode")
public class QFPayRestService implements IQFPayRestService {

    private Logger logger = LoggerFactory.getLogger(QFPayRestService.class);

    @Autowired
    private QFPayHandleService qfPayHandleService;

    @Autowired
    private PayLogService payLogService;


    @Path("notify")
    @POST
    @Produces({ContentType.TEXT_PLAIN_UTF_8})
    public String qfNotify(@Context HttpServletRequest request) {
        String response = null;
        PayDeal payDetail ;
//        String body = null;
        BufferedReader reader = null;
        long beginTime = System.currentTimeMillis();
        try {
            request.setCharacterEncoding("UTF-8");
            String sign = "X-QF-SIGN:" + request.getHeader("X-QF-SIGN");
            StringBuffer requestBody = new StringBuffer();
            String line;
            reader = request.getReader();
            while ((line = reader.readLine()) != null){
                requestBody.append(line);
            }
            //处理
            logger.info("钱方回调请求:" + requestBody.toString());
            payDetail = qfPayHandleService.notifyHandle(sign, requestBody.toString());
            response = BusiConstant.OPERATE_SUCCESS_STRING;

            if (payDetail != null) {//入日志表
                int logStatus = BusiConstant.OPERATE_FAIL;
                if (BusiConstant.OPERATE_SUCCESS_STRING.equals(response)) {
                    logStatus = BusiConstant.OPERATE_SUCCESS;
                }
                payLogService.insertLog(payDetail.getPayId(), payDetail.getPayPaymentNo(), payDetail.getTradeId(), BusiConstant.LOG_PAY_TYPE_PAY_NOTIFY, logStatus, requestBody.toString(), response);
            }
        } catch (BusinessException e) {

            logger.error("业务异常, code {} , message {}。", e.getCode(), e.getMessage());
            response = BusiConstant.OPERATE_FAIL_STRING;

        } catch (Throwable e) {
            logger.error("服务端异常", e);
            response = BusiConstant.OPERATE_FAIL_STRING;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error("操作失败", e);
                }
            }
        }
        try {
            long wasteTime = System.currentTimeMillis() - beginTime;
            logger.info("钱方回调接口耗时 {} 毫秒。", wasteTime);
            String wasteTimeStr = (new StringBuffer(DateUtil.nowStrFormate())).append("[").append(wasteTime).append("]").toString();
            PayMonitorInterfaceModel payMonitorInterfaceModel = new PayMonitorInterfaceModel(request, wasteTimeStr, this.getClass().getSimpleName());
            if (BusiConstant.OPERATE_FAIL_STRING.equals(response))
                payMonitorInterfaceModel.setResultFlag(false);
            AsyncEvent.post(payMonitorInterfaceModel);
        } catch (Throwable e) {
            logger.error("收集监控信息失败", e);
        }
        return response;
    }


}
