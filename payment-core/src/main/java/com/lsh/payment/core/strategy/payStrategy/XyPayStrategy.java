package com.lsh.payment.core.strategy.payStrategy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.model.payEnum.PayChannel;
import com.lsh.payment.core.model.payEnum.TradeType;
import com.lsh.payment.core.service.payment.impl.PayBaseService;
import com.lsh.payment.core.strategy.config.XYPayConfig;
import com.lsh.payment.core.strategy.payVo.xypay.XyPayBaseResult;
import com.lsh.payment.core.strategy.payVo.xypay.XyPayUnifiedOrderResult;
import com.lsh.payment.core.strategy.payVo.xypay.XypayResponse;
import com.lsh.payment.core.util.BigDecimalUtils;
import com.lsh.payment.core.util.HttpClientUtils;
import com.lsh.payment.core.util.MD5Util;
import com.lsh.payment.core.util.XmlUtil;
import com.lsh.payment.core.util.pay.weChatpay.WxSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/23.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class XyPayStrategy implements PayStrategy<XypayResponse> {

    private Logger logger = LoggerFactory.getLogger(XyPayStrategy.class);

    /**
     * 调用对应支付平台组装支付请求报文
     *
     * @param payType  传入需要的支付方式
     * @param paramMap 其他额外需要的参数
     * @return 生成的支付请求
     */
    @Override
    public XypayResponse generatePayParams(int payType, final Map<String, String> paramMap) {
        logger.info("请求兴业下单: " + paramMap);
        BigDecimal amount = BigDecimalUtils.yuanToFen(paramMap.get("payAmount"));//.setScale(0, BigDecimal.ROUND_DOWN);
        String payPaymentNo = paramMap.get("payPaymentNo");
        int channelType = Integer.parseInt(paramMap.get("channelType"));
        //组装报文
        Map<String, String> xypayRequestBody = new HashMap<>(12);

        if (channelType == TradeType.XYWXSM.getCode()) {
            xypayRequestBody.put("service", XYPayConfig.PX_NATIVE);
        } else if (channelType == TradeType.XYALISM.getCode()) {
            xypayRequestBody.put("service", XYPayConfig.PA_NATIVE);
        }

        xypayRequestBody.put("mch_id", XYPayConfig.MAC_ID);
        xypayRequestBody.put("out_trade_no", payPaymentNo);
        xypayRequestBody.put("total_fee", amount.toString());
        xypayRequestBody.put("body", XYPayConfig.GOODS_NAME);
        xypayRequestBody.put("mch_create_ip", XYPayConfig.IP);
        xypayRequestBody.put("notify_url", XYPayConfig.NOTIFY_URL);
        xypayRequestBody.put("nonce_str", MD5Util.generateNonceStr());// 随机字符串
        xypayRequestBody.put("sign", WxSignature.getNewSign(xypayRequestBody, XYPayConfig.KEY));

        String xyPayUnifiedOrderReqXml = XmlUtil.toXml(xypayRequestBody);
        logger.info("兴业下单参数 {}", xyPayUnifiedOrderReqXml);
        String xyPayUnifiedOrderResultXml = HttpClientUtils.doPostXml(XYPayConfig.PAY_API, xyPayUnifiedOrderReqXml);
        //构造返回报文
        XyPayUnifiedOrderResult result = XyPayBaseResult.fromXML(xyPayUnifiedOrderResultXml, XyPayUnifiedOrderResult.class);

        logger.info(payPaymentNo + "-WxPayRefundResult is " + result);

        result.checkResult();

        return this.buildRsp(result, payPaymentNo);
    }

    /**
     * 钱方支付查询返回值
     *
     * @param result    返回结果
     * @param payPaymentNo 请求钱数
     * @return 支付平台返回结果
     */
    private XypayResponse buildRsp(XyPayUnifiedOrderResult result,String payPaymentNo) {

        XypayResponse response = new XypayResponse();
        response.setCode(BusiConstant.PAY_REQUEST_FAIL);
        response.setMsg(BusiConstant.PAY_REQUEST_FAIL_MESSAGE);

        //校验返回参数
        if (result.getStatus().equals("0") && result.getResultCode().equals("0")) {
            response.setCode(BusiConstant.PAY_REQUEST_SUCCESS);
            response.setXyPayUnifiedOrderResult(result);
            JSONObject resResult = new JSONObject();
            resResult.put("qrcode",result.getCodeURL());
            resResult.put("out_trade_no",payPaymentNo);
            response.setMsg(resResult.toJSONString());
        } else {
            response.setCode(result.getErrCode());
            response.setMsg(result.getErrMsg());
            //加下邮件预警
            PayBaseService.monitor(PayChannel.XYPAY.getName(), "兴业返回:" + JSON.toJSONString(result),payPaymentNo);
        }

        return response;
    }
}
