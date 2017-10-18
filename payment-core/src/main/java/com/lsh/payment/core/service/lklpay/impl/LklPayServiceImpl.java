package com.lsh.payment.core.service.lklpay.impl;

import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.baseVo.BaseResponse;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.api.model.lklpay.LklPayContent;
import com.lsh.payment.api.model.lklpay.LklResponse;
import com.lsh.payment.api.model.payment.PaymentRequest;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.payEnum.PayStatus;
import com.lsh.payment.core.model.payEnum.PayWay;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.service.payLog.PayLogService;
import com.lsh.payment.core.service.payment.IPayChannelService;
import com.lsh.payment.core.service.payment.impl.PayBaseService;
import com.lsh.payment.core.strategy.config.LklPayConfig;
import com.lsh.payment.core.util.PayAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/12/2.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service
public class LklPayServiceImpl implements IPayChannelService {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PayBaseService payBaseService;

    @Autowired
    private PayLogService payLogService;


    /**
     * 拉卡拉下单接口
     *
     * @param paymentRequest 下单参数
     * @return BaseResponse
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse prepay(PaymentRequest paymentRequest) {

        PayAssert.notNull(paymentRequest.getTrade_module(), ExceptionStatus.E1002002.getCode(), "Trade_module " + ExceptionStatus.E1002002.getMessage());
        if (!paymentRequest.getPay_way().equals(PayWay.ANDROID.getName())) {
            throw new BusinessException(ExceptionStatus.E1002002.getCode(), "payWay 字段错误");
        }

        return this.payApp(paymentRequest);
    }


    /**
     * lklapp下单请求
     *
     * @param paymentRequest  下单请求请求参数
     * @return 下单请求返回值
     */
    private BaseResponse payApp(PaymentRequest paymentRequest) {

        long t1 = System.currentTimeMillis();
        LklResponse<LklPayContent> lklResponse = new LklResponse<>();

        PayDeal payDeal = payBaseService.prePayment(paymentRequest);

        logger.info("prePayment times is {}", (System.currentTimeMillis() - t1));

        int operateFlag = 0;

        if (payDeal != null) {
            if (payDeal.getPayStatus() != null) {

                lklResponse.setRet(Integer.valueOf(ExceptionStatus.SUCCESS.getCode()));
                lklResponse.setMsg(ExceptionStatus.SUCCESS.getMessage());
                //支付授权
//                payBaseService.authorizePayAndAddPayTask(payDeal);

                LklPayContent content = new LklPayContent();
                content.setTitle(MessageFormat.format(LklPayConfig.TITLE_NAME, payDeal.getTradeId()));
                content.setChannelName(payDeal.getPayChannel());
                content.setAuthedAmount(payDeal.getRequestAmount().toString());
                content.setPayPaymentNo(payDeal.getPayPaymentNo());
                content.setPayStatus(PayStatus.CREATE_PAYMENT.getValue());//TODO 历史原因
                content.setResult(true);

                lklResponse.setContent(content);
                //操作标记
                operateFlag = BusiConstant.OPERATE_SUCCESS;
            } else {

                lklResponse.setRet(Integer.valueOf(ExceptionStatus.FAIL_S.getCode()));
                lklResponse.setMsg(ExceptionStatus.FAIL_S.getMessage());
                //操作标记
                operateFlag = BusiConstant.OPERATE_FAIL;
            }

            //记录操作日志
            payLogService.insertLog(payDeal.getPayId(), payDeal.getPayPaymentNo(), payDeal.getTradeId(), PayStatus.CREATE_PAYMENT.getValue(), operateFlag, JSON.toJSONString(paymentRequest), JSON.toJSONString(lklResponse));

        }

        logger.info("lkl prepay response is {} ", JSON.toJSONString(lklResponse));


        logger.info("lkl payApp response time is {}", System.currentTimeMillis());
        logger.info("lkl payApp times is {}", (System.currentTimeMillis() - t1));

        return lklResponse;
    }
}
