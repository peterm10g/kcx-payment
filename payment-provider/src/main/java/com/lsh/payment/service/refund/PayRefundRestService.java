package com.lsh.payment.service.refund;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;
import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.baseVo.BaseResponse;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.api.model.refund.RefundQueryRequest;
import com.lsh.payment.api.model.refund.RefundRequest;
import com.lsh.payment.api.model.refund.RefundResponse;
import com.lsh.payment.api.service.refund.IPayRefundRestService;
import com.lsh.payment.core.constant.RedisKeyConstant;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.handler.WxRefundHandler;
import com.lsh.payment.core.model.payEnum.PayChannel;
import com.lsh.payment.core.model.payEnum.PayStatus;
import com.lsh.payment.core.model.payEnum.RefundStatus;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.model.refund.PayRefund;
import com.lsh.payment.core.service.RedisService.RedisLockService;
import com.lsh.payment.core.service.payment.impl.PayDealService;
import com.lsh.payment.core.service.refund.PayRefundService;
import com.lsh.payment.core.service.wxPay.IWxRefundService;
import com.lsh.payment.core.strategy.payVo.wxpay.WxPayRefundQueryResult;
import com.lsh.payment.core.util.IdWork.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 17/6/28.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service(protocol = "rest", validation = "true")
@Path("refund")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({ContentType.APPLICATION_JSON_UTF_8})
public class PayRefundRestService implements IPayRefundRestService {

    private final Logger logger = LoggerFactory.getLogger(PayRefundRestService.class);

    @Autowired
    private PayDealService payDealService;
    @Autowired
    private PayRefundService payRefundService;
    @Autowired
    private IWxRefundService wxRefundService;
    @Autowired
    private WxRefundHandler wxRefundHandler;

    @Autowired
    private RedisLockService redisLockService;


    /**
     * 微信 支付宝  钱方 统一退款申请接口
     *
     * @param refundRequest 下单参数
     * @return BaseResponse
     */
    @Override
    @POST
    @Path("apply")
    public BaseResponse refund(RefundRequest refundRequest) {
        BaseResponse baseResponse = new BaseResponse();

        String refundKey = MessageFormat.format(RedisKeyConstant.PAY_REFUND_TRADE_LOCK, refundRequest.getRefund_trade_id());
        try {
            logger.info("RefundRequest info is " + JSON.toJSONString(refundRequest));

            //重复下单锁
            if (!redisLockService.lock(refundKey, refundRequest.getRefund_trade_id())) {
                throw new BusinessException(ExceptionStatus.E1002002.getCode(), "退款太频繁,请稍后再试");
            }
            //是否存在支付记录
            PayDeal payDeal = payDealService.queryPayDealByPayPaymentNo(refundRequest.getPay_payment_no());

            BigDecimal refundFee = new BigDecimal(refundRequest.getRefund_fee());

            this.payDealCheck(payDeal, refundFee, refundRequest);

            //是否有退款记录
            PayRefund payRefund = payRefundService.selectRefundByRefundTradeId(refundRequest.getRefund_trade_id());

            if (payRefund == null) {

                payRefund = this.createPayRefund(refundRequest);
            } else if (payRefund.getRefundStatus() == RefundStatus.REFUND_SUCCESS.getValue()
                    || payRefund.getRefundStatus() == RefundStatus.REFUND_ING.getValue()) {
                RefundResponse refundResponse = new RefundResponse();
//                throw new BusinessException("2006001", "退款申请已成功");
                refundResponse.setPaymemt_refund_no(payRefund.getPaymentRefundNo());
                refundResponse.setRet(Integer.parseInt(ExceptionStatus.SUCCESS.getCode()));
                refundResponse.setMsg(ExceptionStatus.SUCCESS.getMessage());

                return refundResponse;
            }else if (payRefund.getRefundStatus() == RefundStatus.REFUND_EXCEPTION.getValue()){

                throw new BusinessException("2006008", "退款申请失败");
            }

            if (payRefund == null) {

                throw new BusinessException(ExceptionStatus.E2001006.getCode(), "生成退款记录错误");
            }

            baseResponse = wxRefundHandler.refund(payDeal,payRefund);

        } catch (BusinessException e) {

            baseResponse.setRet(Integer.parseInt(e.getCode()));
            baseResponse.setMsg(e.getMessage());
            logger.error("业务异常:code is {},message is {}.", new String[]{e.getCode(), e.getMessage()});
        } catch (Exception e) {

            baseResponse.setRet(Integer.parseInt(ExceptionStatus.E3001001.getCode()));
            baseResponse.setMsg(ExceptionStatus.E3001001.getMessage());
            logger.error("服务端异常", e);
        }finally {
            try {
                redisLockService.unlock(refundKey);
            } catch (Exception e) {
                logger.error("redis 操作异常", e);
            }
        }

        return baseResponse;
    }

    /**
     * 退款查询接口
     *
     * @param refundQueryRequest 下单参数
     * @return BaseResponse
     */
    @Override
    @POST
    @Path("query")
    public BaseResponse refundQuery(RefundQueryRequest refundQueryRequest) {

        PayRefund payRefund = payRefundService.selectRefundByRefundTradeId(refundQueryRequest.getRefund_trade_id());

        BaseResponse baseResponse = new BaseResponse();
        try {

            if (payRefund == null) {
                throw new BusinessException("2001001", "退款记录不存在");
            }

            if (payRefund.getRefundStatus() == RefundStatus.REFUND_ING.getValue()) {
                Map<String, String> wxRefundQueryRequestBody = new HashMap<>(10);
                wxRefundQueryRequestBody.put("refundId", payRefund.getChannelRefundId());//微信退款流水号

                wxRefundQueryRequestBody.put("paymentRefundNo", payRefund.getPaymentRefundNo());//支付平台退款流水号
                WxPayRefundQueryResult wxPayRefundQueryResult = wxRefundService.refundQuery(wxRefundQueryRequestBody);
                if (wxPayRefundQueryResult.getResultCode().equals("SUCCESS") && wxPayRefundQueryResult.getReturnCode().equals("SUCCESS")) {

                    int flag = payRefundService.updateRefundFromWxQueryResult(payRefund, wxPayRefundQueryResult);
                    logger.info("wxPayRefundQueryResult is " + JSON.toJSONString(wxPayRefundQueryResult));
                    baseResponse.setRet(0);
                    if(flag > 0){
                        baseResponse.setMsg("退款成功");
                    }else{
                        baseResponse.setMsg("退款进行中");
                    }

                } else {
                    baseResponse.setRet(2005001);
                    baseResponse.setMsg("查询退款失败");
                }
            } else if (payRefund.getRefundStatus() == RefundStatus.REFUND_SUCCESS.getValue()) {
                baseResponse.setRet(0);
                baseResponse.setMsg("退款成功");
            } else {
                baseResponse.setRet(2005001);
                baseResponse.setMsg("退款失败");
            }

        } catch (BusinessException e) {

            baseResponse.setRet(Integer.parseInt(e.getCode()));
            baseResponse.setMsg(e.getMessage());
            logger.error("业务异常:code is {},message is {}.", new String[]{e.getCode(), e.getMessage()});
        } catch (Exception e) {

            baseResponse.setRet(Integer.parseInt(ExceptionStatus.E3001001.getCode()));
            baseResponse.setMsg(ExceptionStatus.E3001001.getMessage());
            logger.error("服务端异常", e);
        }

        return baseResponse;
    }

    /**
     *
     * @param payDeal
     * @param refundFee
     * @param refundRequest
     * @return
     */
    private boolean payDealCheck(PayDeal payDeal, BigDecimal refundFee, RefundRequest refundRequest) {

        if (payDeal == null) {
            throw new BusinessException(ExceptionStatus.E2001006.getCode(), "支付记录不存在,请确认支付流水号是否正确");
        }

        if (payDeal.getPayStatus() != PayStatus.PAY_SUCCESS.getValue()) {
            throw new BusinessException(ExceptionStatus.E2001006.getCode(), "支付记录状态不合法");
        }

        if (refundFee.compareTo(payDeal.getReceiveAmount()) > 0) {
            throw new BusinessException(ExceptionStatus.E2001006.getCode(), "本次-退款金额大于支付金额");
        }

        List<PayRefund> payRefunds = payRefundService.selectRefundByPayPaymentNo(payDeal.getPayPaymentNo());

        if (payRefunds == null) {
            return true;
        }

        BigDecimal allRefundFee = refundFee;
        for (PayRefund payRefund : payRefunds) {
            if (payRefund.getRefundStatus() == RefundStatus.REFUND_ING.getValue() ||
                    payRefund.getRefundStatus() == RefundStatus.REFUND_SUCCESS.getValue()) {

                allRefundFee = allRefundFee.add(payRefund.getRefundAmount());
            }
        }

        if (allRefundFee.compareTo(payDeal.getReceiveAmount()) > 0) {
            throw new BusinessException(ExceptionStatus.E2001006.getCode(), "总-退款金额大于支付金额");
        }

        return true;
    }

    /**
     *
     * @param refundRequest
     * @return
     */
    private PayRefund createPayRefund(RefundRequest refundRequest) {

        PayRefund addPayRefund = new PayRefund();
        addPayRefund.setPaymentRefundNo(RandomUtil.snowFlakeId());
        addPayRefund.setPayPaymentNo(refundRequest.getPay_payment_no());
        addPayRefund.setCreateAt(new Date());
        addPayRefund.setUpdateAt(new Date());
        addPayRefund.setAmountType("CNY");
        addPayRefund.setRefundAmount(new BigDecimal(refundRequest.getRefund_fee()));
        addPayRefund.setRefundTradeId(refundRequest.getRefund_trade_id());
        addPayRefund.setIsValid(1);
        addPayRefund.setRefundStatus(RefundStatus.REFUND_NEW.getValue());
        addPayRefund.setCallSystem(2);
        addPayRefund.setRefundChannel(PayChannel.WXPAY.getName());

        if (payRefundService.addRefund(addPayRefund) > 0) {
            return addPayRefund;
        }

        return null;
    }
}
