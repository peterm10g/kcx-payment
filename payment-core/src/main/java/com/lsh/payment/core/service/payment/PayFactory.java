package com.lsh.payment.core.service.payment;

import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.api.model.payment.PaymentRequest;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.payEnum.*;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.util.IdWork.RandomUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/27.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class PayFactory {

    /**
     * 创建支付流水记录
     *
     * @param paymentRequest
     * @return
     */
    public static PayDeal create(PaymentRequest paymentRequest) {

        PayDeal payDeal = new PayDeal();

        payDeal.setTradeId(paymentRequest.getTrade_id());

        if (paymentRequest.getTrade_module().equals(TradeModule.ORDER.getName())) {
            payDeal.setTradeModule(TradeModule.ORDER.getName());
        } else if (paymentRequest.getTrade_module().equals(TradeModule.BILL.getName())) {
            payDeal.setTradeModule(TradeModule.BILL.getName());
        }else if (paymentRequest.getTrade_module().equals(TradeModule.RECYCLE.getName())) {
            payDeal.setTradeModule(TradeModule.RECYCLE.getName());
        } else {
            throw new BusinessException(ExceptionStatus.E2001002.getCode(), ExceptionStatus.E2001002.getMessage());
        }

//        支付流水号,支付订单号(第三版)支付平台订单号
        String payId = RandomUtil.snowFlakeId();


        payDeal.setPayId(payId);
        payDeal.setPayPaymentNo(payId);

        if (paymentRequest.getPay_way().equals(PayWay.IOS.getName())) {
            payDeal.setPayWay(PayWay.IOS.getName());
        } else if (paymentRequest.getPay_way().equals(PayWay.ANDROID.getName())) {
            payDeal.setPayWay(PayWay.ANDROID.getName());
        } else if (paymentRequest.getPay_way().equals(PayWay.H5.getName())) {
            payDeal.setPayWay(PayWay.H5.getName());
        } else {

            throw new BusinessException(ExceptionStatus.E2001002.getCode(), ExceptionStatus.E2001002.getMessage());
        }

        payDeal.setRequestAmount(new BigDecimal(paymentRequest.getRequest_amount()));

        payDeal.setPayType(PayType.PAY.getCode());

        String channelType = paymentRequest.getChannel_type();

        payDeal.setPayChannel(getPayChannel(Integer.parseInt(channelType)));

        payDeal.setPayStatus(PayStatus.PAYING.getValue());
        payDeal.setOperateStatus(OperateStatus.PAYMENT_NO_CALLBACK.getCode());
        payDeal.setCreatedAt(new Date());
        payDeal.setUpdatedAt(new Date());
        //支付模块类型，1-主支付模块，2-副支付模块
        payDeal.setModuleType(ModubleType.BASE.getCode());//默认
        //调用系统,默认1，商城
        if(paymentRequest.getSystem().compareTo(CallSystem.GROUPON.getCode()) == 0){
            payDeal.setSystem(CallSystem.GROUPON.getCode());
        }else{
            payDeal.setSystem(CallSystem.YG.getCode());
        }

        Object ext = paymentRequest.getExt();

        if (ext != null) {
            payDeal.setExt(String.valueOf(ext));
        }

        payDeal.setLshNotifyUrl(paymentRequest.getNotify_url());
        payDeal.setTradeType(Integer.parseInt(paymentRequest.getChannel_type()));

        return payDeal;
    }


    /**
     * PayChannel 和 ChannelType 对应关系
     */
    public static String getPayChannel(int channelType) {

        switch (channelType) {
            case 1:
                return PayChannel.ALIPAY.getName();
            case 2:
                return PayChannel.WXPAY.getName();
            case 3:
                return PayChannel.ALIPAY.getName();
            case 4:
                return PayChannel.WXPAY.getName();
            case 5:
                return PayChannel.LKLPAY.getName();
            case 6:
                return PayChannel.QFPAY.getName();
            case 8:
                return PayChannel.QFPAY.getName();
            case 10:
                return PayChannel.XYPAY.getName();
            case 12:
                return PayChannel.XYPAY.getName();
            case 13:
                return PayChannel.CMBCPAY.getName();
            case 14:
                return PayChannel.CMBCPAY.getName();
            case 15:
                return PayChannel.ALLINPAY.getName();
            case 16:
                return PayChannel.ALLINPAY.getName();
            default:
                throw new BusinessException(ExceptionStatus.E2001002.getCode(), "payChannel " + ExceptionStatus.E2001002.getMessage());
        }
    }

}
