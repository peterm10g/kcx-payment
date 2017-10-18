package com.lsh.payment.core.service.wxPay.impl;

import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.api.model.baseVo.QueryContent;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.model.payEnum.PayStatus;
import com.lsh.payment.core.model.payment.PayDeal;
import com.lsh.payment.core.service.payment.IPayQueryChannelService;
import com.lsh.payment.core.service.payment.builder.QueryDirector;
import com.lsh.payment.core.service.payment.builder.WxQueryBuilder;
import com.lsh.payment.core.service.payment.impl.PayBaseService;
import com.lsh.payment.core.service.payment.impl.PayQueryBaseService;
import com.lsh.payment.core.strategy.payVo.baseVo.BasePayResponse;
import com.lsh.payment.core.strategy.queryStrategy.QueryStrategyContext;
import com.lsh.payment.core.util.PayAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/26.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service
@Transactional(readOnly = true)
public class WxQueryServiceImpl implements IPayQueryChannelService {

    private static Logger logger = LoggerFactory.getLogger(PayQueryBaseService.class);

    @Autowired
    private PayBaseService payBaseService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public QueryContent query(PayDeal payDeal) {
        Map<String, Object> params = new HashMap<>(7);

        params.put("channelId", payDeal.getChannelTransaction());
        params.put("out_trade_no", payDeal.getPayPaymentNo());
        params.put("pay_way", payDeal.getPayWay());
        params.put("system", payDeal.getSystem());

        QueryStrategyContext context = new QueryStrategyContext();

        BasePayResponse basePayResponse = context.queryPayStatusByParams(BusiConstant.WECHATQUERY, params);

        //检查结果对象
        PayAssert.notNull(basePayResponse, ExceptionStatus.E2001013.getCode(), ExceptionStatus.E2001013.getMessage());

        WxQueryBuilder wxQueryBuilder = new WxQueryBuilder();
        QueryDirector director = new QueryDirector(wxQueryBuilder);
        director.queryParse(basePayResponse, payDeal);
        QueryContent content = wxQueryBuilder.getResult();

        if (content.getPayCode() != null) {
            int payCode = content.getPayCode();
            if (payCode == PayStatus.PAY_SUCCESS.getValue() || payCode == (PayStatus.PAY_FAIL.getValue())) {

                payBaseService.updPayDeal(wxQueryBuilder.getUpdatePaydeal(), payDeal);
            }
        }

        return content;
//        return parse(basePayResponse, payDeal);
    }

//    /**
//     * 解析微信参数
//     * @param basePayResponse 参数
//     * @param payDeal         支付记录
//     * @return 结果
//     */
//    private Map<String, Object> parse(BasePayResponse basePayResponse, PayDeal payDeal) {
//
//        Map<String, Object> content = new HashMap<>();
//        PayDeal payUpd = new PayDeal();
//        payUpd.setPayId(payDeal.getPayId());
//        payUpd.setQueryTime(new Date());
//
//        content.put("trade_id", payDeal.getTradeId());
//        content.put("Channel_transaction", payDeal.getChannelTransaction());
//
//        if (basePayResponse == null || basePayResponse.getCode() == null || basePayResponse.getCode().equals("2")) {
//            content.put("pay_code", PayStatus.PAYING.getValue());
//            content.put("pay_msg", PayStatus.PAYING.getName());
//
//            return content;
//        } else {
//
//            return this.getWxResult(basePayResponse, payDeal);
//        }
//    }


//    /**
//     * @param basePayResponse 参数
//     * @param payDeal      支付记录
//     * @return 结果
//     */
//    private Map<String, Object> getWxResult(BasePayResponse basePayResponse, PayDeal payDeal) {
//
//        Map<String, Object> content;
//
//        PayDeal payUpd = new PayDeal();
//        payUpd.setPayId(payDeal.getPayId());
//        payUpd.setQueryTime(new Date());
//
//        //支付签名成功
//        WxQueryResponse weChatQueryResponse = (WxQueryResponse) basePayResponse;
//
//        //以下字段在return_code 和result_code都为SUCCESS的时候有返回
//        //SUCCESS—支付成功 ,REFUND—转入退款 ,NOTPAY—未支付 ,CLOSED—已关闭
//        //REVOKED—已撤销（刷卡支付）,USERPAYING--用户支付中 ,PAYERROR--支付失败(其他原因，如银行返回失败)
//        String trateStatus = weChatQueryResponse.getTrade_state();
//
//        payUpd.setChannelTransaction(weChatQueryResponse.getTransaction_id());
//
//        content = new HashMap<>();
//        if (trateStatus.equals(WxPayConfig.WX_SUCCESS)) {
//
//            content.put("pay_code", PayStatus.PAY_SUCCESS.getValue());
//            content.put("pay_msg", PayStatus.PAY_SUCCESS.getName());
//            content.put("trade_state", weChatQueryResponse.getTrade_state());
//
//            //更新表
//            payUpd.setPayStatus(PayStatus.PAY_SUCCESS.getValue());
//            payBaseService.updPayDeal(payUpd, payDeal);
//        } else if (trateStatus.equals(WxPayConfig.WX_CLOSED) || trateStatus.equals(WxPayConfig.WX_PAYERROR)) {
//
//            logger.info("查询支付状态 : trade_state = " + weChatQueryResponse.getTrade_state());
//
//            content.put("trade_state", weChatQueryResponse.getTrade_state());
//            content.put("pay_code", PayStatus.PAY_FAIL.getValue());
//            content.put("pay_msg", PayStatus.PAY_FAIL.getName());
//
//            //交易失败,更新数据库
//            payUpd.setPayStatus(PayStatus.PAY_FAIL.getValue());
//            payBaseService.updPayDeal(payUpd, null);
//        } else if (trateStatus.equals(WxPayConfig.WX_NOTPAY) || trateStatus.equals(WxPayConfig.WX_USERPAYING)) {
//
//            //支付未完成
//            logger.info("查询支付状态 : trade_state = " + weChatQueryResponse.getTrade_state());
//
//            content.put("trade_state", weChatQueryResponse.getTrade_state());
//            content.put("pay_code", PayStatus.PAYING.getValue());
//            content.put("pay_msg", PayStatus.PAYING.getName());
//            //查询次数加一
////            payTaskService.addQueryTimes(payDeal.getPayId());
//        } else {
//            //支付异常
//            logger.info("查询支付状态 : trade_state = " + weChatQueryResponse.getTrade_state());
//        }
//
//
//        return content;
//    }
}
