package com.lsh.payment.core.service.lklpay;

import com.alibaba.fastjson.JSON;
import com.lakala.sign.LKLApiException;
import com.lakala.sign.LKLSignature;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.api.model.lklpay.LklQueryRequest;
import com.lsh.payment.core.constant.RedisKeyConstant;
import com.lsh.payment.core.dao.redis.RedisStringDao;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.Async.EmailModel;
import com.lsh.payment.core.model.payEnum.OperateStatus;
import com.lsh.payment.core.model.payEnum.PayStatus;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.service.AsyncService.AsyncEvent;
import com.lsh.payment.core.service.email.EmailConfig;
import com.lsh.payment.core.service.payment.impl.PayBaseService;
import com.lsh.payment.core.service.payment.impl.PayDealService;
import com.lsh.payment.core.strategy.config.LklPayConfig;
import com.lsh.payment.core.util.BigDecimalUtils;
import com.lsh.payment.core.util.DateUtil;
import com.lsh.payment.core.util.PayAssert;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/2.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service
@Transactional(readOnly = true)
public class LklNotifyService {

    private static Logger logger = LoggerFactory.getLogger(LklNotifyService.class);

    private final static String charset = "UTF-8";
    //拉卡拉交易类型码  支付
    private final static String LKL_TRADE_TYPE_PAY = "012001";
    //拉卡拉交易类型码  冲正
    private final static String LKL_TRADE_TYPE_FLUSH = "012010";

    @Autowired
    private PayDealService payDealService;
    @Autowired
    private PayBaseService payBaseService;
    @Autowired
    private EmailConfig emailConfig;
    @Autowired
    private RedisStringDao redisStringDao;


    /**
     * @param dataMap 参数
     * @param sign    签名
     * @return        结果
     */
    @Transactional(rollbackFor = Exception.class)
    public PayDeal lklPayNotify(Map<String, String> dataMap, String sign) {

//        boolean unsignReslut = this.checkSign(dataMap, sign);

        //签名验证失败 TODO
        if (!this.checkSign(dataMap, sign)) {
            throw new BusinessException(ExceptionStatus.E2001014.getCode(), ExceptionStatus.E2001014.getMessage());
        }

        String tradeType = dataMap.get("trade_type");
        PayAssert.notNull(tradeType, ExceptionStatus.E2001006.getCode(), "拉卡拉[trade_type]:[支付回调结果参数为空]");
        //冲正处理
        if(tradeType.equals(LKL_TRADE_TYPE_FLUSH)){
            String refernumber = dataMap.get("refernumber");
            PayAssert.notNull(refernumber, ExceptionStatus.E2001006.getCode(), "[refernumber]:[" + ExceptionStatus.E2001006.getMessage() + "]");

            PayDeal payDeal = payDealService.queryPayDealByChannelTransaction(refernumber);

            if (payDeal == null || StringUtils.isBlank(payDeal.getPayPaymentNo())) {
                logger.info("Lakla 回调数据中refernumber对应支付数据不存在 " + JSON.toJSONString(dataMap));
                throw new BusinessException(ExceptionStatus.E2001004.getCode(), ExceptionStatus.E2001004.getMessage());
            }

            StringBuilder content = new StringBuilder("拉卡拉支付冲正数据: ").append(System.getProperty("line.separator"))
                    .append("拉卡拉账户号: ").append(dataMap.get("mch_id")).append(System.getProperty("line.separator"))
                    .append("拉卡拉设备编码: ").append(dataMap.get("device_info")).append(System.getProperty("line.separator"))
                    .append("拉卡拉支付流水号: ").append(dataMap.get("transaction_id")).append(System.getProperty("line.separator"))
                    .append("拉卡拉参考号: ").append(dataMap.get("refernumber")).append(System.getProperty("line.separator"))
                    .append("支付平台流水号:").append(dataMap.get("out_trade_no")).append(System.getProperty("line.separator"))
                    .append("总金额: ").append(dataMap.get("total_fee")).append(System.getProperty("line.separator"))
                    .append("支付金额: ").append(dataMap.get("pay_amt")).append(System.getProperty("line.separator"))
                    .append("支付时间: ").append(dataMap.get("time_end"));
            logger.info("异步发送邮件 content is " + JSON.toJSONString(dataMap));
            AsyncEvent.post(new EmailModel(content.toString(),"拉卡拉支付冲正",emailConfig.financeReceivers()));

            return payDeal;
        }
        //支付平台流水号
        String payPaymentNo = dataMap.get("out_trade_no");

        if (StringUtils.isBlank(payPaymentNo)) {

            StringBuilder content = new StringBuilder("拉卡拉异步回调 : ").append(System.getProperty("line.separator"))
                    .append("拉卡拉账户号: ").append(dataMap.get("mch_id")).append(System.getProperty("line.separator"))
                    .append("拉卡拉设备编码: ").append(dataMap.get("device_info")).append(System.getProperty("line.separator"))
                    .append("拉卡拉支付流水号: ").append(dataMap.get("transaction_id")).append(System.getProperty("line.separator"))
                    .append("拉卡拉参考号: ").append(dataMap.get("refernumber")).append(System.getProperty("line.separator"))
                    .append("支付结果:").append(dataMap.get("result_code")).append(System.getProperty("line.separator"))
                    .append("总金额: ").append(dataMap.get("total_fee")).append(System.getProperty("line.separator"))
                    .append("支付金额: ").append(dataMap.get("pay_amt")).append(System.getProperty("line.separator"))
                    .append("支付时间: ").append(dataMap.get("time_end"));
            logger.info("拉卡拉异步回调 异步发送邮件 content is " + JSON.toJSONString(dataMap));
            AsyncEvent.post(new EmailModel(content.toString(),"拉卡拉异步回调异常",emailConfig.mailReceivers()));

            throw new BusinessException(ExceptionStatus.E2001006.getCode(),"[out_trade_no]:[" + ExceptionStatus.E2001006.getMessage() + "]");
        }

        PayDeal payDeal = payDealService.queryPayDealByPayPaymentNo(payPaymentNo);

        if (payDeal == null || StringUtils.isBlank(payDeal.getPayPaymentNo())) {
            logger.info("Lakla 回调数据中支付平台流水号对应支付数据不存在 " + JSON.toJSONString(dataMap));
            throw new BusinessException(ExceptionStatus.E2001004.getCode(), ExceptionStatus.E2001004.getMessage());
        }

        PayAssert.notNull(dataMap.get("result_code"), ExceptionStatus.E2001006.getCode(), "拉卡拉[result_code]:[支付回调结果参数为空]");
        if (dataMap.get("result_code").equals(ExceptionStatus.SUCCESS_S.getMessage())) {
            //支付成功
            this.paySuccess(payDeal, dataMap);
        } else if (dataMap.get("result_code").equals(ExceptionStatus.FAIL_S.getMessage())) {
            //支付失败
            this.payFail(payDeal, dataMap);
        } else {
            throw new BusinessException(ExceptionStatus.E2001009.getCode(), "拉卡拉[result_code]:[支付回调结果参数异常]");
        }

        return payDeal;
    }

    /**
     * 支付成功处理
     *
     * @param payDeal         支付记录
     * @param lklQueryRequest 参数
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean lklpaySuccess(PayDeal payDeal, LklQueryRequest lklQueryRequest) {

        PayDeal pd = new PayDeal();
        pd.setPayId(payDeal.getPayId());
        //数据库支付记录已修改为支付成功
        if (payDeal.getPayStatus() == PayStatus.PAY_SUCCESS.getValue()) {
            logger.info("支付结果重复回调 : " + JSON.toJSONString(lklQueryRequest));
            return true;
        }

        //成功回调时,如果数据库支付记录状态不是成功 则修改为成功,
        if (payDeal.getPayStatus() != PayStatus.PAY_SUCCESS.getValue()) {
            pd.setPayStatus(PayStatus.PAY_SUCCESS.getValue());
            pd.setOperateStatus(OperateStatus.PAYMENT_CALLBACK.getCode());
            PayAssert.notNull(lklQueryRequest.getChannelTransaction(), ExceptionStatus.E2001004.getCode(), ExceptionStatus.E2001004.getMessage());
            logger.info("pay_payment_no is " + payDeal.getPayPaymentNo() + ", 第三方支付平台流水号" + lklQueryRequest.getChannelTransaction());

            pd.setChannelTransaction(lklQueryRequest.getChannelTransaction());
            BigDecimal receiveAmount = new BigDecimal(lklQueryRequest.getReceiveAmount());
            pd.setReceiveAmount(receiveAmount);
            if (payDeal.getRequestAmount().compareTo(pd.getReceiveAmount()) != 0) {
                logger.info("订单信息 : " + JSON.toJSONString(payDeal));
                logger.info("pay_payment_no is " + payDeal.getPayPaymentNo() + " , 订单金额与支付金额不相等 订单金额 : " + payDeal.getRequestAmount().toString() + "支付金额: " + pd.getReceiveAmount().toString());
                throw new BusinessException(ExceptionStatus.E2001008.getCode(), ExceptionStatus.E2001008.getMessage());
            }

            pd.setPayTime(new Date());
            pd.setDoneTime(new Date());
            payBaseService.updPayDeal(pd, payDeal);

            return true;
        }

        return false;
    }

    /**
     * 支付成功处理
     *
     * @param payDeal 支付记录
     * @param dataMap 参数
     */
    private void paySuccess(PayDeal payDeal, Map<String, String> dataMap) {

        PayDeal pd = new PayDeal();
        pd.setPayId(payDeal.getPayId());
        //数据库支付记录已修改为支付成功
        if (payDeal.getPayStatus() == PayStatus.PAY_SUCCESS.getValue()) {
            logger.info("支付结果重复回调 : " + JSON.toJSONString(dataMap));
            return;
        }

        //成功回调时,如果数据库支付记录状态不是成功 则修改为成功,
        if (payDeal.getPayStatus() != PayStatus.PAY_SUCCESS.getValue()) {
            pd.setPayStatus(PayStatus.PAY_SUCCESS.getValue());
            pd.setOperateStatus(OperateStatus.PAYMENT_CALLBACK.getCode());

            PayAssert.notNull(dataMap.get("refernumber"), ExceptionStatus.E2001004.getCode(), ExceptionStatus.E2001004.getMessage());
            PayAssert.notNull(dataMap.get("time_end"), ExceptionStatus.E2001009.getCode(), ExceptionStatus.E2001009.getMessage());

            logger.info("pay_payment_no is {}, 第三方支付平台流水号 {}。", payDeal.getPayPaymentNo(), dataMap.get("refernumber"));

            pd.setChannelTransaction(dataMap.get("refernumber"));
            if (StringUtils.isNotBlank(dataMap.get("pay_amt"))) {
                pd.setReceiveAmount(BigDecimalUtils.fenToYuan(dataMap.get("pay_amt")));
                if (payDeal.getRequestAmount().compareTo(pd.getReceiveAmount()) != 0) {
                    logger.info("订单信息 : {}", JSON.toJSONString(payDeal));
                    logger.info("pay_payment_no is [{}] , 订单金额与支付金额不相等 订单金额 : [{}] , 支付金额: [{}]", new String[]{payDeal.getPayPaymentNo(), payDeal.getRequestAmount().toString(), pd.getReceiveAmount().toString()});
                    throw new BusinessException(ExceptionStatus.E2001008.getCode(), ExceptionStatus.E2001008.getMessage());
                }
            } else {
                throw new BusinessException(ExceptionStatus.E2001008.getCode(), "支付金额不能为空");
            }

            logger.info("lklpay paytime is [" + dataMap.get("time_end") + "] db paytime is [" + DateUtil.parsePayDate(dataMap.get("time_end")) + "]");

            pd.setPayTime(DateUtil.parsePayDate(dataMap.get("time_end")));
            pd.setDoneTime(new Date());

            payBaseService.updPayDeal(pd, payDeal);

        }

    }

    /**
     * 支付失败处理
     *
     * @param payDeal 支付记录
     * @param dataMap 参数
     */
    private void payFail(PayDeal payDeal, Map<String, String> dataMap) {

        logger.info(" lakla 支付回调 result_code is : " + dataMap.get("result_code"));
        logger.info(" lakla 支付回调 err_code is : " + dataMap.get("err_code"));
        logger.info(" lakla 支付回调 err_code_des is : " + dataMap.get("err_code_des"));

        PayDeal pd = new PayDeal();
        pd.setPayId(payDeal.getPayId());
        //数据库支付记录已修改为支付失败
        if (payDeal.getPayStatus() == PayStatus.PAY_FAIL.getValue()) {
            logger.info("支付结果重复回调 : " + JSON.toJSONString(dataMap));
            return;
        }

        //支付失败回调时,如果支付记录状态不是支付失败,则修改为支付失败
        if (payDeal.getPayStatus() != PayStatus.PAY_FAIL.getValue()) {
            pd.setPayStatus(PayStatus.PAY_FAIL.getValue());
            pd.setOperateStatus(OperateStatus.PAYMENT_CALLBACK.getCode());
            if (StringUtils.isNotBlank(dataMap.get("refernumber"))) {
                pd.setChannelTransaction(dataMap.get("refernumber"));
            }
            pd.setDoneTime(new Date());

            payBaseService.updPayDeal(pd, null);
        }
    }


    /**
     * 验证签名
     * @param dataMap 参数签名
     * @param sign    签名
     * @return        是否通过
     */
    private boolean checkSign(Map<String, String> dataMap, String sign) {

        String publicKey = redisStringDao.get(RedisKeyConstant.LKL_KEY);
        logger.info("refernumber is {},redis lkl publicKey is {} ", dataMap.get("refernumber"),publicKey);
        if (StringUtils.isBlank(publicKey)){
            String path = LklPayConfig.LKL_PERM_PATH + LklPayConfig.LKL_PERM_NAME;
            // 获取到的公匙
            publicKey = getPublicKey(path);
            redisStringDao.set(RedisKeyConstant.LKL_KEY,publicKey);
        }

        // 把数字证书放到map里准备验证?
        dataMap.put("sign", sign);

        boolean unsignReslut = false;
        try {
            unsignReslut = LKLSignature.rsaCheckV1(dataMap, publicKey, charset);
            logger.info("unsignReslut is : {}", unsignReslut);
        } catch (LKLApiException e) {
            logger.error("lakala sign LKLApiException");
        }

        return unsignReslut;

    }

    /**
     * 读取公钥
     * @param path 公钥路径
     * @return     公钥
     */
    private static String getPublicKey(String path) {
        File perm = new File(path);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(perm), "UTF-8"))) {
            String publickey = br.readLine();
            logger.info("lakala publickey is : {}", publickey);
            return publickey;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    //客户业务
//            System.out.println("mch_id         = " + dataMap.get("mch_id"));             //商户号
//            System.out.println("device_info    = " + dataMap.get("device_info"));        //设备号
//            System.out.println("nonce_str      = " + dataMap.get("nonce_str"));          //随机字符串
//            System.out.println("sign           = " + dataMap.get("sign"));               //签名
//            System.out.println("result_code    = " + dataMap.get("result_code"));        //业务结果
//            System.out.println("err_code       = " + dataMap.get("err_code"));           //错误代码
//            System.out.println("err_code_des   = " + dataMap.get("err_code_des"));       //错误代码描述
//            System.out.println("openid         = " + dataMap.get("openid"));             //用户标识
//            System.out.println("trade_type     = " + dataMap.get("trade_type"));         //交易类型
//            System.out.println("total_fee      = " + dataMap.get("total_fee"));          //总金额
//            System.out.println("transaction_id = " + dataMap.get("transaction_id"));     //商户支付订单号
//            System.out.println("out_trade_no   = " + dataMap.get("out_trade_no"));       //商户订单号
//            System.out.println("attach         = " + dataMap.get("attach"));             //商家数据包
//            System.out.println("time_end       = " + dataMap.get("time_end"));           //支付完成时间
//            System.out.println("Pay_type       = " + dataMap.get("Pay_type"));           //支付方式
//            System.out.println("card_no        = " + dataMap.get("card_no"));            //交易卡号
//            System.out.println("pay_amt        = " + dataMap.get("pay_amt"));            //支付金额
//            System.out.println("batchbillno    = " + dataMap.get("batchbillno"));        //批次号
//            System.out.println("systraceno     = " + dataMap.get("systraceno"));         //凭证号
//            System.out.println("orderid_scan   = " + dataMap.get("orderid_scan"));       //扫码订单号
//            System.out.println("refernumber    = " + dataMap.get("refernumber"));        //系统参考号
//            System.out.println("bank_type      = " + dataMap.get("bank_type"));          //付款银行
//            System.out.println("fee_type       = " + dataMap.get("fee_type"));           //货币种类
//            System.out.println("cash_fee       = " + dataMap.get("cash_fee"));           //现金支付金额
//            System.out.println("cash_fee_type  = " + dataMap.get("cash_fee_type"));      //现金支付货币类型
//            System.out.println("coupon_fee     = " + dataMap.get("coupon_fee"));         //代金券或立减优惠金额
//            System.out.println("coupon_count   = " + dataMap.get("coupon_count"));       //代金券或立减优惠使用数量
//            System.out.println("coupon_id_$n   = " + dataMap.get("coupon_id_$n"));       //代金券或立减优惠ID
//            System.out.println("coupon_fee_$n  = " + dataMap.get("coupon_fee_$n"));      //单个代金券或立减优惠支付金额
//            System.out.println("is_subscribe   = " + dataMap.get("is_subscribe"));       //是否关注公众账号
    //业务成功返回
//            jsonObject.put("return_code", "SUCCESS");
//            jsonObject.put("return_msg", "ok");


}
