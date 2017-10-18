package com.lsh.payment.core.service.AsyncService;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.lsh.payment.api.model.payment.PaymentRequest;
import com.lsh.payment.core.constant.RedisKeyConstant;
import com.lsh.payment.core.dao.payLog.PayLogDao;
import com.lsh.payment.core.dao.redis.RedisListDao;
import com.lsh.payment.core.dao.redis.RedisStringDao;
import com.lsh.payment.core.model.Async.*;
import com.lsh.payment.core.model.PayTask.PayTask;
import com.lsh.payment.core.model.payEnum.PayChannel;
import com.lsh.payment.core.model.payEnum.TradeType;
import com.lsh.payment.core.model.payLog.PayLog;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.service.businessNotify.BusinessNotifyService;
import com.lsh.payment.core.service.email.SendEmailService;
import com.lsh.payment.core.service.payTask.PayTaskService;
import com.lsh.payment.core.service.qfpay.impl.QfQueryServiceImpl;
import com.lsh.payment.core.service.xypay.impl.XyQueryServiceImpl;
import com.lsh.payment.core.strategy.config.QFPayConfig;
import com.lsh.payment.core.strategy.config.WxPayConfig;
import com.lsh.payment.core.util.DateUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/12/22
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.service.AsyncService
 * desc:异步处理监听类
 */
@Component
public class AsyncEventListener {
    private Logger logger = LoggerFactory.getLogger(AsyncEventListener.class);

    @Autowired
    private SendEmailService sendEmailService;

    @Autowired
    private BusinessNotifyService businessNotifyService;

    @Autowired
    private RedisStringDao redisStringDao;

    @Autowired
    private RedisListDao redisListDao;

    @Value("${call.rate.timeout}")
    private long callRateTimeout;

    @Value("${fail.rate.timeout}")
    private long failRateTimeout;

    @Value("${qf.query.timeout}")
    private long qfQueryTimeout;

    @Autowired
    private PayLogDao payLogDao;

    @Autowired
    private QfQueryServiceImpl qfQueryService;

    @Autowired
    private PayTaskService payTaskService;

    @Autowired
    private XyQueryServiceImpl xyQueryService;

    /**
     * 发送邮件
     *
     * @param emailModel 监听类
     */
    @Subscribe
    @AllowConcurrentEvents
    public void sendEmail(EmailModel emailModel) {
        boolean emailFlag = sendEmailService.send(emailModel.getContent(), emailModel.getTitle(), emailModel.getToEmails());
        if (!emailFlag) {
            redisListDao.rightPush(RedisKeyConstant.EMAIL, emailModel.getContent().toString());
        }
    }

    /**
     * 结果回调
     *
     * @param resultNotifyModel 监听类
     */
    @Subscribe
    @AllowConcurrentEvents
    public void sendNotify(ResultNotifyModel resultNotifyModel) {

        businessNotifyService.dealNotify(resultNotifyModel.getPayDeal());
    }

    /**
     * 系统监控
     *
     * @param payMonitorInterfaceModel 监听类
     */
    @Subscribe
    @AllowConcurrentEvents
    public void payMonitor(PayMonitorInterfaceModel payMonitorInterfaceModel) {
        //统一下单,分别处理
        StringBuffer className = new StringBuffer(payMonitorInterfaceModel.getClassName());
        if (payMonitorInterfaceModel.getClassName().equals("PaymentService")) {
            //统一下单,分别进行统计1支付宝, 2微信，5lkl，6微信扫码，8支付宝扫码
            PaymentRequest paymentRequest = (PaymentRequest) payMonitorInterfaceModel.getBaseRequest();
            className.append(TradeType.getTradeTypeByCode(Integer.parseInt(paymentRequest.getChannel_type())));
            payMonitorInterfaceModel.setClassName(className.toString());
        }
        this.monitorCalledRate(payMonitorInterfaceModel);
        this.monitorFailNum(payMonitorInterfaceModel);
        this.monitorFailRate(payMonitorInterfaceModel);
        this.monitorHandleLong(payMonitorInterfaceModel);
    }

    /**
     * 系统监控之被调频率监控
     *
     * @param payMonitorInterfaceModel 监控统计信息
     */
    private void monitorCalledRate(PayMonitorInterfaceModel payMonitorInterfaceModel) {
        String interfaceCalledRateKey = MessageFormat.format(RedisKeyConstant.CALLED_RATE, payMonitorInterfaceModel.getClassName(), this.getTimeStr());
        redisStringDao.set(interfaceCalledRateKey, interfaceCalledRateKey);
        redisStringDao.expire(interfaceCalledRateKey, callRateTimeout);
    }

    /**
     * 系统监控之连续失败次数监控
     *
     * @param payMonitorInterfaceModel 监控统计信息
     */
    private void monitorFailNum(PayMonitorInterfaceModel payMonitorInterfaceModel) {
        String interfaceFailkey = MessageFormat.format(RedisKeyConstant.FAIL_NUM, payMonitorInterfaceModel.getClassName());
        if (payMonitorInterfaceModel.isResultFlag()) {
            //成功则删除
            redisStringDao.delete(interfaceFailkey);
        } else {
            //失败加1
            redisStringDao.increment(interfaceFailkey);
        }
    }


    /**
     * 系统监控之失败频率监控
     *
     * @param payMonitorInterfaceModel 监控统计信息
     */
    private void monitorFailRate(PayMonitorInterfaceModel payMonitorInterfaceModel) {
        String failRateKey = MessageFormat.format(RedisKeyConstant.FAIL_RATE, payMonitorInterfaceModel.getClassName(), this.getTimeStr());
        if (!payMonitorInterfaceModel.isResultFlag()) {
            //失败写redis
            redisStringDao.set(failRateKey, failRateKey);
            redisStringDao.expire(failRateKey, failRateTimeout);
        }
    }

    /**
     * 系统监控之接口耗时监控
     *
     * @param payMonitorInterfaceModel 监控统计信息
     */
    private void monitorHandleLong(PayMonitorInterfaceModel payMonitorInterfaceModel) {
        String payMachine = System.getenv("PAY_MACHINE");
        //脚本决定耗时多长时间发邮件
        StringBuffer machineInfo = new StringBuffer();
        if (StringUtils.isNotEmpty(payMachine)) {
            machineInfo.append("(").append(payMachine).append(")");
        }
//        String interfaceTimeRedisKey = this.getInterfaceTimeRedisKey(payMonitorInterfaceModel.getClassName());
        redisListDao.rightPush(this.getInterfaceTimeRedisKey(payMonitorInterfaceModel.getClassName()), machineInfo.append(payMonitorInterfaceModel.getWasteTime()).toString());
    }

    /**
     * 获取接口耗时的redis key
     *
     * @param className 类名称
     * @return redis key
     */
    private String getInterfaceTimeRedisKey(String className) {
        return MessageFormat.format(RedisKeyConstant.HANDLE_LONG, className);
    }

    /**
     * 生成频率监控redis的key值后缀,时间字符串加两位随机数
     */
    private static String getTimeStr() {
        return DateUtil.nowStr() + RandomStringUtils.randomNumeric(2);
    }


    /**
     * 下单监控,调第三方下单失败,组织邮件内容存入redis
     *
     * @param callThirdPayFailModel 监听类
     */
    @Subscribe
    @AllowConcurrentEvents
    public void callThirdPayFailMonitor(CallThirdPayFailModel callThirdPayFailModel) {
        StringBuffer content;
        String paymentNoStr = "";
        String flag = callThirdPayFailModel.getFlag();
        String errorMsg = callThirdPayFailModel.getErrorMsg();
        Object req = callThirdPayFailModel.getReq();
        if (flag.equals(QFPayConfig.PAY_API)) {
            try {
                Map<String, Object> mapP = (Map<String, Object>) req;
                paymentNoStr = mapP.get("out_trade_no") + "";
            } catch (Exception e) {
                logger.error("get paymentNo error", e);
            }
            content = new StringBuffer("调 [钱方] 预下单接口失败 : ").append(System.getProperty("line.separator"));

        } else if (flag.equals(WxPayConfig.PAY_API)) {
            try {
                String xmlP = (String) req;
                paymentNoStr = "" + xmlP.split("<out_trade_no>")[1].split("</out_trade_no>")[0];

            } catch (Exception e) {
                logger.error("get paymentNo error", e);
            }

            content = new StringBuffer("调 [微信] 预下单接口失败 : ").append(System.getProperty("line.separator"));
        } else if (flag.equals(PayChannel.QFPAY.getName())) {

            content = new StringBuffer("调 [钱方] 预下单接口失败 : ").append(System.getProperty("line.separator"));
            paymentNoStr = (String) req;
        } else {
            return;
        }

        content.append("机器号 :").append(this.getMachineInfo()).append(System.getProperty("line.separator"))
                .append("支付平台流水号 : ").append(paymentNoStr).append(System.getProperty("line.separator"))
                .append("预下单时间 : ").append(DateUtil.defaultTimestamp(new Date())).append(System.getProperty("line.separator"))
                .append("错误或异常信息 : ").append(errorMsg);

        logger.info("http error send email: {} , {} " + content.toString(), flag, errorMsg);
        redisListDao.rightPush(RedisKeyConstant.EMAIL, content.toString());
    }

    /**
     * 获取日志设备信息
     *
     * @return 设备信息
     */
    private String getMachineInfo() { //TODO 抽象成公共方法
        String machineInfo = System.getenv("PAY_MACHINE");
        if (StringUtils.isBlank(machineInfo)) {
            return "";
        }
        return machineInfo;
    }


    /**
     * 插入日志表
     *
     * @param payLog 日志类
     */
    @Subscribe
    @AllowConcurrentEvents
    public void addPayLog(PayLog payLog) {
        try {
            payLogDao.insert(payLog);
        } catch (Exception e) {
            logger.error("记录日志异常", e);
        }
    }

    /**
     * 钱方查询
     *
     * @param queryModel 监听类
     */
    @Subscribe
    @AllowConcurrentEvents
    public void qfQuery(QueryModel queryModel) {
        logger.info("钱方异步查询开始");
        //5s一次
        String qfQuerykey = MessageFormat.format(RedisKeyConstant.QF_QUERY_KEY, queryModel.getPayDeal().getChannelTransaction());
        String qfQueryLock = redisStringDao.get(qfQuerykey);
        if (StringUtil.isBlank(qfQueryLock)) {
            redisStringDao.set(qfQuerykey, "1");
            redisListDao.expire(qfQuerykey, qfQueryTimeout);
            qfQueryService.query(queryModel.getPayDeal());
        }
        logger.info("钱方异步查询结束");
    }

    /**
     * 钱方查询
     *
     * @param queryModel 监听类
     */
    @Subscribe
    @AllowConcurrentEvents
    public void xyQuery(XyQueryModel queryModel) {
        logger.info("钱方异步查询开始");
        //5s一次
        String qfQuerykey = MessageFormat.format(RedisKeyConstant.XY_QUERY_KEY, queryModel.getPayDeal().getPayPaymentNo());
        String qfQueryLock = redisStringDao.get(qfQuerykey);
        if (StringUtil.isBlank(qfQueryLock)) {
            redisStringDao.set(qfQuerykey, "1");
            redisListDao.expire(qfQuerykey, qfQueryTimeout);
            xyQueryService.query(queryModel.getPayDeal());
        }
        logger.info("钱方异步查询结束");
    }

    /**
     * 插入payTask表
     *
     * @param payTaskModel 监听类
     */
    @Subscribe
    @AllowConcurrentEvents
    public void initPayTask(payTaskModel payTaskModel) {
        logger.info("插入payTask表开始");
        try {
            PayTask payTask = new PayTask();
            PayDeal payDeal = payTaskModel.getPayDeal();

            payTask.setTradeId(payDeal.getTradeId());
            payTask.setPayId(payDeal.getPayId());
            payTask.setPayPaymentNo(payDeal.getPayPaymentNo());
            payTask.setChannelTransaction(payDeal.getChannelTransaction());
            payTask.setTradeModule(payDeal.getTradeModule());
            payTask.setCreatedAt(new Date());
            payTask.setUpdatedAt(new Date());

            payTaskService.addPayTask(payTask);

        } catch (Exception ex) {

            logger.error(" add payTask error ", ex);

        }
        logger.info("插入payTask表结束");
    }

}
