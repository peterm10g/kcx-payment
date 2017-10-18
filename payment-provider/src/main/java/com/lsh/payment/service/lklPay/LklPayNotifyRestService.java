package com.lsh.payment.service.lklPay;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.api.model.lklpay.LklPayNotifyResponse;
import com.lsh.payment.api.service.lklPay.ILklPayNotifyRestService;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.Async.PayMonitorInterfaceModel;
import com.lsh.payment.core.model.payEnum.PayStatus;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.service.AsyncService.AsyncEvent;
import com.lsh.payment.core.service.lklpay.LklNotifyService;
import com.lsh.payment.core.service.payLog.PayLogService;
import com.lsh.payment.core.util.DateUtil;
import com.lsh.payment.core.util.PayAssert;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/2.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service(protocol = "rest")
@Path("lkl")
@Produces({ContentType.APPLICATION_JSON_UTF_8})
public class LklPayNotifyRestService implements ILklPayNotifyRestService {

    private static Logger logger = LoggerFactory.getLogger(LklPayNotifyRestService.class);

    @Autowired
    private LklNotifyService lklNotifyService;

    @Autowired
    private PayLogService payLogService;

    @POST
    @Path("notify")
    public LklPayNotifyResponse lklNotify(@FormParam("sign") String sign, @FormParam("data") String data) {

        LklPayNotifyResponse lklPayNotifyResponse = new LklPayNotifyResponse();
        long beginTime = System.currentTimeMillis();
        try {

            logger.info("lklpay notify sign is : {}", sign);
            logger.info("lklpay notify data is : {}", data);

            PayAssert.notNull(sign, ExceptionStatus.E2001006.getCode(), "支付回调参数sign为空");
            PayAssert.notNull(data, ExceptionStatus.E2001006.getCode(), "支付回调参数data为空");

            Map<String, String> dataMap = toLinkedHashMap(data);
            PayDeal payDeal = lklNotifyService.lklPayNotify(dataMap, sign);
            if (payDeal == null) {
                throw new BusinessException(ExceptionStatus.E2001009.getCode(), "拉卡拉支付回调处理失败");
            }

            if (StringUtils.isNotBlank(payDeal.getPayPaymentNo())) {
                lklPayNotifyResponse.setReturn_code(ExceptionStatus.SUCCESS_S.getMessage());
                lklPayNotifyResponse.setReturn_msg("OK");

                //记录操作日志
                payLogService.insertLog(payDeal.getPayId(), payDeal.getPayPaymentNo(), payDeal.getTradeId(), PayStatus.PAY_SUCCESS.getValue(), BusiConstant.OPERATE_SUCCESS, JSON.toJSONString(dataMap), JSON.toJSONString(lklPayNotifyResponse));
            } else {
                lklPayNotifyResponse.setReturn_code(ExceptionStatus.FAIL_S.getMessage());
                lklPayNotifyResponse.setReturn_msg("数据签名失败或其他错误");//TODO 具体再定

                //记录操作日志
                payLogService.insertLog(payDeal.getPayId(), payDeal.getPayPaymentNo(), payDeal.getTradeId(), PayStatus.PAY_FAIL.getValue(), BusiConstant.OPERATE_FAIL, JSON.toJSONString(dataMap), JSON.toJSONString(lklPayNotifyResponse));
            }
        } catch (BusinessException e) {

            lklPayNotifyResponse.setReturn_code(ExceptionStatus.FAIL_S.getMessage());
            lklPayNotifyResponse.setReturn_msg(e.getMessage());
            logger.error("业务异常:" + e.getCode() + e.getMessage());
        } catch (Throwable e) {

            lklPayNotifyResponse.setReturn_code(ExceptionStatus.FAIL_S.getMessage());
            lklPayNotifyResponse.setReturn_msg("服务端异常");
            logger.error("服务端异常", e);
        }

        try {
            long wasteTime = System.currentTimeMillis() - beginTime;
            logger.info("拉卡拉回调接口耗时 {} 毫秒。", wasteTime);
            String wasteTimeStr = (new StringBuffer(DateUtil.nowStrFormate())).append("[").append(wasteTime).append("]").toString();
            PayMonitorInterfaceModel payMonitorInterfaceModel = new PayMonitorInterfaceModel(data, wasteTimeStr, this.getClass().getSimpleName());
            if (!ExceptionStatus.SUCCESS_S.getMessage().equals(lklPayNotifyResponse.getReturn_code())){
                payMonitorInterfaceModel.setResultFlag(false);
            }
            AsyncEvent.post(payMonitorInterfaceModel);
        } catch (Throwable e) {
            logger.error("收集监控信息失败", e);
        }
        return lklPayNotifyResponse;
    }


    /**
     * json字符串数据转化成Map
     *
     * @param json String
     * @return json对应的map
     **/
    private HashMap<String, String> toLinkedHashMap(String json) {
        LinkedHashMap<String, String> data = JSONObject.parseObject(json, LinkedHashMap.class);
        return data;
    }
}
