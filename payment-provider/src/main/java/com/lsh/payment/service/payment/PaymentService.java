package com.lsh.payment.service.payment;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;
import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.baseVo.BaseResponse;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.api.model.payment.PaymentRequest;
import com.lsh.payment.api.service.payment.IPayRestService;
import com.lsh.payment.core.constant.RedisKeyConstant;
import com.lsh.payment.core.dao.redis.RedisStringDao;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.Async.PayMonitorInterfaceModel;
import com.lsh.payment.core.model.payEnum.PayChannel;
import com.lsh.payment.core.model.payEnum.PayService;
import com.lsh.payment.core.model.payEnum.TradeModule;
import com.lsh.payment.core.model.payEnum.TradeType;
import com.lsh.payment.core.service.AsyncService.AsyncEvent;
import com.lsh.payment.core.service.RedisService.RedisLockService;
import com.lsh.payment.core.service.payment.IPayChannelService;
import com.lsh.payment.core.util.DateUtil;
import com.lsh.payment.core.util.PayAssert;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/8.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service(protocol = "rest", validation = "true")
@Path("payment")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({ContentType.APPLICATION_JSON_UTF_8})
public class PaymentService implements IPayRestService {

    private final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private static Map<String, String> strategyMap;

    @Autowired
    private Map<String, IPayChannelService> payServices;

    @Autowired
    private RedisLockService redisLockService;

    @Autowired
    private RedisStringDao redisStringDao;

    @Value("${payment.create.strategy}")
    private String strategy;

    @Value("${payment.create.percentage}")
    private Integer percentage;

    static {
        strategyMap = new HashMap<>();
//        strategyMap.put(TradeType.QFWXSM.getCode() + "", TradeType.XYWXSM.getCode() + "");
//        strategyMap.put(TradeType.QFALISM.getCode() + "", TradeType.XYALISM.getCode() + "");

//        strategyMap.put(TradeType.QFWXSM.getCode() + "", TradeType.CMBCWXSM.getCode() + "");
//        strategyMap.put(TradeType.QFALISM.getCode() + "", TradeType.CMBCALISM.getCode() + "");

        strategyMap.put(TradeType.QFWXSM.getCode() + "", TradeType.ALLINWXSM.getCode() + "");
        strategyMap.put(TradeType.QFALISM.getCode() + "", TradeType.ALLINALISM.getCode() + "");
    }


    @POST
    @Path("prepayment")
    public BaseResponse prePayment(PaymentRequest paymentRequest) {
        BaseResponse baseResponse = new BaseResponse();

        logger.info("支付平台统一下单请求: {} .", JSON.toJSONString(paymentRequest));
        long beginTime = System.currentTimeMillis();
        String key = MessageFormat.format(RedisKeyConstant.PAY_TRADE_LOCK, paymentRequest.getTrade_id());
        try {
            //重复下单锁
            if (!redisLockService.lock(key, paymentRequest.getTrade_id())) {
                throw new BusinessException(ExceptionStatus.E1002002.getCode(), "下单太频繁,请稍后再试");
            }

            if (!paymentRequest.getNotify_url().startsWith("http")) {
                throw new BusinessException(ExceptionStatus.E2001002.getCode(), "notify_url 参数异常");
            }

            PayAssert.isAmount(paymentRequest.getRequest_amount().toString(), ExceptionStatus.E1002001.getCode(), "支付金额不合法,小数点后最多两位,且大于0,例如(12.25,0.88)");


            this.strategyFilter(paymentRequest);

            String serviceName = PayService.getServiceByCode(paymentRequest.getChannel_type());
            if (StringUtils.isBlank(serviceName)) {
                throw new BusinessException(ExceptionStatus.E2001002.getCode(), "channelType 参数异常");
            }

            logger.info("paymentRequest.getSystem is " + paymentRequest.getSystem());
            if (paymentRequest.getSystem() == null) {
                paymentRequest.setSystem(1);
            }
            logger.info("paymentRequest.getSystem is must " + paymentRequest.getSystem());

            baseResponse = payServices.get(serviceName).prepay(paymentRequest);
        } catch (BusinessException e) {
            baseResponse.setRet(Integer.parseInt(e.getCode()));
            baseResponse.setMsg(e.getMessage());
            logger.error("业务异常:code is {},message is {}.", new String[]{e.getCode(), e.getMessage()});
        } catch (Exception e) {
            baseResponse.setRet(Integer.parseInt(ExceptionStatus.E3001001.getCode()));
            baseResponse.setMsg(ExceptionStatus.E3001001.getMessage());
            logger.error("服务端异常", e);
        } finally {
            try {
                redisLockService.unlock(key);
            } catch (Exception e) {
                logger.error("redis 操作异常", e);
            }
        }

        try {
            long wasteTime = System.currentTimeMillis() - beginTime;
            logger.info("tradeid is {} 统一下单接口耗时 {} 毫秒。", paymentRequest.getTrade_id(), wasteTime);
            String wasteTimeStr = (new StringBuffer(DateUtil.nowStrFormate())).append("[").append(wasteTime).append("]").toString();
            PayMonitorInterfaceModel payMonitorInterfaceModel = new PayMonitorInterfaceModel(paymentRequest, wasteTimeStr, this.getClass().getSimpleName());
            if (!baseResponse.getRet().toString().equals(ExceptionStatus.SUCCESS.getCode()))
                payMonitorInterfaceModel.setResultFlag(false);
            AsyncEvent.post(payMonitorInterfaceModel);
        } catch (Throwable e) {
            logger.error("收集监控信息失败", e);
        }

        return baseResponse;
    }

    /**
     * @param paymentRequest
     */
    private void strategyFilter(PaymentRequest paymentRequest) {
        String filterPrefix = "【下单扫码策略】[" + paymentRequest.getTrade_id() + "] ";

        if (!strategy.equals("yes")) {
            logger.info("{} strategy is {}", filterPrefix, strategy);
            return;
        }

        if (Integer.parseInt(paymentRequest.getChannel_type()) <= TradeType.LKL.getCode()) {
            logger.info("{} Channel_type is {}", filterPrefix, paymentRequest.getChannel_type());
            return;
        }

        // TODO Channel_type 处理 待优化

        PayAssert.notNull(paymentRequest.getTrade_module(), ExceptionStatus.E1002002.getCode(), ExceptionStatus.E1002002.getMessage());
        //bill 扫码支付用兴业银行
        if (paymentRequest.getTrade_module().equals(TradeModule.BILL.getName())) {
            String chanType = strategyMap.get(paymentRequest.getChannel_type());
            if (StringUtils.isNotBlank(chanType)) {
                paymentRequest.setChannel_type(chanType);
            }
        } else {
            String timesKey = RedisKeyConstant.PAY_TRADE_TIMES;
            String smKey = MessageFormat.format(RedisKeyConstant.PAY_TRADE_SM_CHANNEL, paymentRequest.getTrade_id());
            String payChannel = redisStringDao.get(smKey);
            if (StringUtils.isNotBlank(payChannel)) {
                String chanType = "";
                if (payChannel.equals(PayChannel.XYPAY.getName())) {
                    chanType = strategyMap.get(paymentRequest.getChannel_type());
                    if(StringUtils.isNotBlank(chanType)){
                        paymentRequest.setChannel_type(chanType);
                    }
                }

                logger.info("{} redis 渠道值 = {}", filterPrefix, payChannel + " : " + chanType);
                return;
            }

            Long tradeTimes = redisStringDao.increment(timesKey);
            logger.info("{} redis tradeTimes = {}", filterPrefix, tradeTimes);
            int lastNum = 0;
            if (tradeTimes != null) {
                String tradeTimesStr = tradeTimes.toString();
                String _lastNum = tradeTimesStr.substring(tradeTimesStr.length() - 1);
                if (StringUtils.isNotBlank(_lastNum)) {
                    lastNum = Integer.parseInt(_lastNum);
                }
            }
            logger.info("{} strategy lastNum = {}", filterPrefix, lastNum);
            if (lastNum < percentage) {

                String chanType = strategyMap.get(paymentRequest.getChannel_type());
                logger.info("{} strategyMap channelType = {}", filterPrefix, chanType);
                if (StringUtils.isNotBlank(chanType)) {
                    paymentRequest.setChannel_type(chanType);

                    redisStringDao.set(smKey, PayChannel.XYPAY.getName());
                    //24 * 60 * 60
                    redisStringDao.expire(smKey, 86400);
                }
            } else {
                String chanType = paymentRequest.getChannel_type();
                logger.info("{} paymentRequest channelType = {}", filterPrefix, chanType);
                if (StringUtils.isNotBlank(chanType)) {
                    paymentRequest.setChannel_type(chanType);

                    redisStringDao.set(smKey, PayChannel.WXPAY.getName());
                    //24 * 60 * 60
                    redisStringDao.expire(smKey, 86400);
                }
            }
        }

        logger.info("{} strategy lastNum = {}", filterPrefix, JSON.toJSONString(paymentRequest));
    }

}
