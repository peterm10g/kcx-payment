package com.lsh.payment.service.aliPay;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;
import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.service.aliPay.IAliPayRestService;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.Async.PayMonitorInterfaceModel;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.service.AsyncService.AsyncEvent;
import com.lsh.payment.core.service.alipay.impl.AliNotifyServiceImpl;
import com.lsh.payment.core.service.payLog.PayLogService;
import com.lsh.payment.core.util.DateUtil;
import com.lsh.payment.core.util.HttpParamsUntil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/10/31
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.service.qfPay
 * desc:
 */
@Service(protocol = "rest")
@Path("alipay")
@Produces({ContentType.TEXT_PLAIN_UTF_8})
public class AliPayRestService implements IAliPayRestService {

    private static String CHARSET = "UTF-8";

    @Autowired
    private AliNotifyServiceImpl aliNotifyServiceImpl;

    @Autowired
    private PayLogService payLogService;

    private Logger logger = LoggerFactory.getLogger(AliPayRestService.class);

    @Path("notify")
    @POST
    public String aliNotify() {
        HttpServletRequest request = (HttpServletRequest) RpcContext.getContext().getRequest();

        String aliPayResponse;
        long beginTime = System.currentTimeMillis();

        try {
            request.setCharacterEncoding(CHARSET);
            Map<String, String> requestMap = HttpParamsUntil.getAlipayParamMap(request);
            logger.info("支付宝回调请求参数:" + JSON.toJSONString(requestMap));
            PayDeal payDeal = aliNotifyServiceImpl.notifyHandle(requestMap);
            aliPayResponse = BusiConstant.OPERATE_SUCCESS_STRING;

            if (payDeal != null) {//入日志表
                int logStatus = BusiConstant.OPERATE_FAIL;
                if (BusiConstant.OPERATE_SUCCESS_STRING.equals(aliPayResponse)) {
                    logStatus = BusiConstant.OPERATE_SUCCESS;
                }
                payLogService.insertLog(payDeal.getPayId(), payDeal.getPayPaymentNo(), payDeal.getTradeId(), BusiConstant.LOG_PAY_TYPE_PAY_NOTIFY, logStatus, JSON.toJSONString(requestMap), aliPayResponse);
            }
        } catch (BusinessException e) {

            aliPayResponse = BusiConstant.OPERATE_FAIL_STRING;
            logger.error("业务异常:" + e.getCode() + e.getMessage());
        } catch (Exception e) {
            aliPayResponse = BusiConstant.OPERATE_FAIL_STRING;
            logger.error("服务端异常", e);
        }

        try {
            long wasteTime = System.currentTimeMillis() - beginTime;
            logger.info("支付宝回调接口耗时 {} 毫秒。", wasteTime);
            String wasteTimeStr = (new StringBuffer(DateUtil.nowStrFormate())).append("[").append(wasteTime).append("]").toString();
            PayMonitorInterfaceModel payMonitorInterfaceModel = new PayMonitorInterfaceModel(request, wasteTimeStr, this.getClass().getSimpleName());
            if (BusiConstant.OPERATE_FAIL_STRING.equals(aliPayResponse))
                payMonitorInterfaceModel.setResultFlag(false);
            AsyncEvent.post(payMonitorInterfaceModel);
        } catch (Throwable e) {
            logger.error("收集监控信息失败", e);
        }

        return aliPayResponse;
    }


}

