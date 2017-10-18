package com.lsh.payment.core.strategy.payStrategy;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.payEnum.PayChannel;
import com.lsh.payment.core.model.payEnum.TradeType;
import com.lsh.payment.core.service.payment.impl.PayBaseService;
import com.lsh.payment.core.strategy.config.QFPayConfig;
import com.lsh.payment.core.strategy.payVo.qfpay.QFpayResponse;
import com.lsh.payment.core.util.BigDecimalUtils;
import com.lsh.payment.core.util.DateUtil;
import com.lsh.payment.core.util.pay.qfpay.QFSignature;
import com.lsh.payment.core.util.HttpClientUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/23.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class QfPayStrategy implements PayStrategy<QFpayResponse> {

    private Logger logger = LoggerFactory.getLogger(QfPayStrategy.class);

    /**
     * 调用对应支付平台组装支付请求报文
     *
     * @param payType  传入需要的支付方式
     * @param paramMap 其他额外需要的参数
     * @return 生成的支付请求
     */
    @Override
    public QFpayResponse generatePayParams(int payType, final Map<String, String> paramMap) {
        logger.info("请求钱方下单: " + paramMap);
        BigDecimal amount = BigDecimalUtils.yuanToFen(paramMap.get("payAmount"));//.setScale(0, BigDecimal.ROUND_DOWN);
        String payPaymentNo = paramMap.get("payPaymentNo");
        int channelType = Integer.parseInt(paramMap.get("channelType"));
        //组装报文
        Map<String, Object> qfpayRequestBody = new HashMap<>(12);
        qfpayRequestBody.put("txamt", amount);
        qfpayRequestBody.put("txcurrcd", QFPayConfig.RMB);
        qfpayRequestBody.put("out_trade_no", payPaymentNo);
        qfpayRequestBody.put("txdtm", DateUtil.defaultTime(new Date()));
        qfpayRequestBody.put("goods_name", QFPayConfig.GOODS_NAME);

        if (channelType == TradeType.QFWXSM.getCode()) {
            qfpayRequestBody.put("pay_type", QFPayConfig.PAY_TYPE_WX);
        } else if (channelType == TradeType.QFALISM.getCode()) {
            qfpayRequestBody.put("pay_type", QFPayConfig.PAY_TYPE_ALI);
        }
        //签名
        String sign = QFSignature.getSign(qfpayRequestBody);
        //报文头
        Map<String, String> qfPayRequestHead = new HashMap<>();
        qfPayRequestHead.put("X-QF-APPCODE", QFPayConfig.APP_CODE);
        qfPayRequestHead.put("X-QF-SIGN", sign);
        //请求钱方
        logger.info("钱方下单参数 {}", JSON.toJSONString(qfpayRequestBody));
        Map<String, String> callRsp = HttpClientUtils.doPostForQF(QFPayConfig.PAY_API, qfpayRequestBody, qfPayRequestHead);
        //构造返回报文
        QFpayResponse qFpayRresponse = this.mkRsp(callRsp, amount);
        logger.info("请求钱方支付,支付流水," + payPaymentNo + "返回:" + JSON.toJSONString(qFpayRresponse));
        return qFpayRresponse;
    }

    /**
     * 钱方支付查询返回值
     *
     * @param callRsp   返回结果
     * @param reqAmount 请求钱数
     * @return 支付平台返回结果
     */
    private QFpayResponse mkRsp(Map<String, String> callRsp, BigDecimal reqAmount) {
        if (StringUtils.isBlank(callRsp.get("httpStr"))||StringUtils.isBlank(callRsp.get("sign"))){
            logger.error("调钱方下单失败");
            throw new BusinessException(ExceptionStatus.E2001005.getCode(), ExceptionStatus.E2001005.getMessage());
        }
        QFpayResponse response = new QFpayResponse();
        response.setCode(BusiConstant.PAY_REQUEST_FAIL);
        response.setMsg(BusiConstant.PAY_REQUEST_FAIL_MESSAGE);
        String rspBody = callRsp.get("httpStr");

        logger.info("rspBody = " + rspBody);

        String rspSign = callRsp.get("sign");
        if (QFSignature.check(rspBody, rspSign)) {
            JSONObject jsonObject = JSONObject.fromObject(rspBody);
            response = (QFpayResponse) JSONObject.toBean(jsonObject, QFpayResponse.class);
            //校验返回参数
            if (response.getTxamt().equals(reqAmount.toString())) {
                if (response.getRespcd().equals(QFPayConfig.RESPCD_SUCCESS)) {
                    response.setCode(BusiConstant.PAY_REQUEST_SUCCESS);
                    response.setMsg(rspBody);
                } else {
                    response.setCode(response.getRespcd());
                    response.setMsg(response.getResperr());
                    //加下邮件预警
                    PayBaseService.monitor(PayChannel.QFPAY.getName(), "钱方返回:" + response.getRespcd() + response.getResperr(), response.getOut_trade_no());
                }
            }
        }

        return response;
    }
}
