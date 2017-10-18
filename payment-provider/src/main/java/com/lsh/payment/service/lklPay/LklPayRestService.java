package com.lsh.payment.service.lklPay;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;
import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.api.model.lklpay.LklQueryRequest;
import com.lsh.payment.api.model.lklpay.LklQueryResponse;
import com.lsh.payment.api.service.lklPay.ILklPayRestService;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.service.lklpay.LklNotifyService;
import com.lsh.payment.core.service.payment.impl.PayDealService;
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
 * Created by peter on 16/10/26.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service(protocol = "rest", validation = "true")
@Path("lkl")
@Produces({ContentType.APPLICATION_JSON_UTF_8})
public class LklPayRestService implements ILklPayRestService {

    private static Logger logger = LoggerFactory.getLogger(LklPayRestService.class);

    @Autowired
    private LklNotifyService lklNotifyService;

    @Autowired
    private PayDealService payDealService;


    /**
     * 拉卡拉
     *
     * @param lklQueryRequest 请求参数
     * @return LklQueryResponse
     */
    @POST
    @Path("queryForTradeId")
    @Consumes({MediaType.APPLICATION_JSON})
    public LklQueryResponse lklQuery(LklQueryRequest lklQueryRequest) {

        LklQueryResponse lklQueryResponse = new LklQueryResponse();

        logger.info("lkl aysn notify lklQueryRequest is {} ", JSON.toJSONString(lklQueryRequest));

        try {

            long t0 = System.currentTimeMillis();
            PayDeal payDeal = payDealService.queryPayDealByPayPaymentNo(lklQueryRequest.getPayPaymentNo());

            if (payDeal == null) {
                throw new BusinessException(ExceptionStatus.E2001004.getCode(), ExceptionStatus.E2001004.getMessage());
            }

            long t1 = System.currentTimeMillis();
            lklNotifyService.lklpaySuccess(payDeal,lklQueryRequest);
            long t2 = System.currentTimeMillis();
            logger.info(" lkl aysn notify method lklpaySuccess time is : [" +(t2 - t1)+ "]");

            lklQueryResponse.setTradeId(payDeal.getTradeId());
            lklQueryResponse.setTradeModule(payDeal.getTradeModule());
            lklQueryResponse.setRet(Integer.parseInt(ExceptionStatus.SUCCESS.getCode()));
            lklQueryResponse.setMsg(ExceptionStatus.SUCCESS.getMessage());
            logger.info(" lkl aysn notify time is : [" +(t2 - t0)+ "]");
        } catch (BusinessException e) {

            lklQueryResponse.setRet(Integer.parseInt(e.getCode()));
            lklQueryResponse.setMsg(e.getMessage());
            logger.error("业务异常:" + e.getCode() + e.getMessage());
        } catch (Throwable e) {

            lklQueryResponse.setRet(Integer.parseInt(ExceptionStatus.E3001001.getCode()));
            lklQueryResponse.setMsg(ExceptionStatus.E3001001.getMessage());
            logger.error("服务端异常", e);
        }

        return lklQueryResponse;
    }


}
