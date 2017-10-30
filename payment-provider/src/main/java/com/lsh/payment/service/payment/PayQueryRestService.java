package com.lsh.payment.service.payment;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;
import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.baseVo.BaseResponse;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.api.model.baseVo.QueryContent;
import com.lsh.payment.api.model.baseVo.QueryResponse;
import com.lsh.payment.api.model.payment.PaymentQueryRequest;
import com.lsh.payment.api.service.payment.IPayQueryRestService;
import com.lsh.payment.core.dao.redis.RedisStringDao;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.Async.PayMonitorInterfaceModel;
import com.lsh.payment.core.service.AsyncService.AsyncEvent;
import com.lsh.payment.core.service.RedisService.RedisLockService;
import com.lsh.payment.core.service.payment.impl.PayQueryBaseService;
import com.lsh.payment.core.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Project Name: lsh-payment
 * Created by miaozhuang on 16/11/7.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service(protocol = "rest", validation = "true")
@Path("payment")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({ContentType.APPLICATION_JSON_UTF_8})
public class PayQueryRestService implements IPayQueryRestService {

    private final Logger logger = LoggerFactory.getLogger(PayQueryRestService.class);

    @Autowired
    private PayQueryBaseService payQueryBaseService;

    @Autowired
    private RedisLockService redisLockService;

    @Autowired
    private RedisStringDao redisStringDao;

    @POST
    @Path("query")
    public BaseResponse queryPayStatus(PaymentQueryRequest paymentQueryRequest) {

        QueryResponse queryResponse = new QueryResponse();

        long beginTime = System.currentTimeMillis();
        try {

            logger.info("统一查询接口参数 {}", JSON.toJSONString(paymentQueryRequest));

            QueryContent content = payQueryBaseService.queryPayStatus(paymentQueryRequest);
            queryResponse.setContent(content);

            queryResponse.setRet(Integer.parseInt(ExceptionStatus.SUCCESS.getCode()));
            queryResponse.setMsg(ExceptionStatus.SUCCESS.getMessage());

        } catch (BusinessException e) {

            queryResponse.setRet(Integer.parseInt(e.getCode()));
            queryResponse.setMsg(e.getMessage());
            logger.error("业务异常, code {}, message {}.", e.getCode(), e.getMessage());
        } catch (Exception e) {

            queryResponse.setRet(Integer.parseInt(ExceptionStatus.E3001001.getCode()));
            queryResponse.setMsg(ExceptionStatus.E3001001.getMessage());
            logger.error("服务端异常", e);
        }

        try {
            long wasteTime = System.currentTimeMillis() - beginTime;
            logger.info("统一查询接口耗时 {} 毫秒。", wasteTime);
            String wasteTimeStr = (new StringBuffer(DateUtil.nowStrFormate())).append("[").append(wasteTime).append("]").toString();
            PayMonitorInterfaceModel payMonitorInterfaceModel = new PayMonitorInterfaceModel(paymentQueryRequest, wasteTimeStr, this.getClass().getSimpleName());
            if (!queryResponse.getRet().toString().equals(ExceptionStatus.SUCCESS.getCode()))
                payMonitorInterfaceModel.setResultFlag(false);
            AsyncEvent.post(payMonitorInterfaceModel);
        } catch (Throwable e) {
            logger.error("收集监控信息失败", e);
        }

        return queryResponse;
    }


}
