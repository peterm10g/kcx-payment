package com.lsh.payment.core.strategy.payStrategy;

import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.cmbcpay.UnfiedOrderReq;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.model.payEnum.PayChannel;
import com.lsh.payment.core.model.payEnum.TradeType;
import com.lsh.payment.core.service.payment.impl.PayBaseService;
import com.lsh.payment.core.strategy.config.CMBCPayConfig;
import com.lsh.payment.core.strategy.payVo.cmbcpay.CMBCpayResponse;
import com.lsh.payment.core.util.BigDecimalUtils;
import com.lsh.payment.core.util.DateUtil;
import com.lsh.payment.core.util.pay.cmbcpay.HttpPostRequest;
import com.lsh.payment.core.util.pay.cmbcpay.Md5SignUtil;
import com.lsh.payment.core.util.pay.cmbcpay.ObjectAndMapConvert;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/23.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class CMBCPayStrategy implements PayStrategy<CMBCpayResponse> {

    private Logger logger = LoggerFactory.getLogger(CMBCPayStrategy.class);

    /**
     * 调用对应支付平台组装支付请求报文
     *
     * @param payType  传入需要的支付方式
     * @param paramMap 其他额外需要的参数
     * @return 生成的支付请求
     */
    @Override
    public CMBCpayResponse generatePayParams(int payType, final Map<String, String> paramMap) {
        String prefix = "请求民生下单 ";
        logger.info("{}参数: {}", prefix, paramMap);
        BigDecimal amount = BigDecimalUtils.yuanToFen(paramMap.get("payAmount"));
        String payPaymentNo = paramMap.get("payPaymentNo");
        int channelType = Integer.parseInt(paramMap.get("channelType"));
        //组装报文
//        Map<String, String> payRequestBody = new HashMap<>(12);
//        payRequestBody.put("merNo", CMBCPayConfig.MAC_ID);
//        payRequestBody.put("orderNo", payPaymentNo);
//        payRequestBody.put("reqId", payPaymentNo);
//        payRequestBody.put("reqTime", DateUtil.formatTime(new Date()));
//        payRequestBody.put("goods_name", QFPayConfig.GOODS_NAME);
//        payRequestBody.put("amount",amount.toString());
//        payRequestBody.put("notifyUrl",CMBCPayConfig.NOTIFY_URL);
//
//        if (channelType == TradeType.CMBCWXSM.getCode()) {
//            payRequestBody.put("channelFlag",CMBCPayConfig.PAY_CHANNEL_WX);
//        } else if (channelType == TradeType.CMBCALISM.getCode()) {
//            payRequestBody.put("channelFlag",CMBCPayConfig.PAY_CHANNEL_ALI);
//        }

        UnfiedOrderReq req = new UnfiedOrderReq();

        req.setAmount(amount.toString());
        req.setNotifyUrl(CMBCPayConfig.NOTIFY_URL);
        req.setOrderNo(payPaymentNo);
        req.setReqId(payPaymentNo);
        req.setMerNo(CMBCPayConfig.MAC_ID);
        req.setReqTime(DateUtil.formatTime(new Date()));
        if (channelType == TradeType.CMBCWXSM.getCode()) {
            req.setChannelFlag(CMBCPayConfig.PAY_CHANNEL_WX);
        } else if (channelType == TradeType.CMBCALISM.getCode()) {
            req.setChannelFlag(CMBCPayConfig.PAY_CHANNEL_ALI);
        }

        req.setSignIn(Md5SignUtil.md5Sign(ObjectAndMapConvert.convertObjectToMap(req), CMBCPayConfig.KEY));

//        logger.info("{} 下单参数 {}", prefix,JSON.toJSONString(payRequestBody));
//        String callRsp = HttpClientUtils.doPostStr(CMBCPayConfig.PAY_API + CMBCPayConfig.PAY_NATIVE, payRequestBody);

        JSONObject jsonRet = HttpPostRequest.sendRequest(ObjectAndMapConvert.convertObjectToMap(req), CMBCPayConfig.PAY_API + CMBCPayConfig.PAY_NATIVE);
//        {"codeUrl":"https://qr.alipay.com/bax00793wbl6ibbnskap00ad","desc":"下单成功","orderNo":"905702283080105984","reqId":"905702283080105984","result":"0000","signOut":"EE12D7319884E392EC2116D534D163C0","transId":"20170907155748994732"}

        return this.buildRsp(jsonRet, payPaymentNo);
    }

    /**
     * 民生下单返回值
     *
     * @param jsonRet 返回结果
     * @return 支付平台返回结果
     */
    private CMBCpayResponse buildRsp(JSONObject jsonRet, String payPaymentNo) {

        CMBCpayResponse response = new CMBCpayResponse();
        response.setCode(BusiConstant.PAY_REQUEST_FAIL);
        response.setMsg(BusiConstant.PAY_REQUEST_FAIL_MESSAGE);

        logger.info("支付流水号 {} callRsp = {}", payPaymentNo, jsonRet.toString());

        String result = jsonRet.getString("result");
        if (StringUtils.isNotBlank(result) && result.equals(CMBCPayConfig.TRADE_STATE_SUCCESS)) {
            response = JSON.parseObject(jsonRet.toString(), CMBCpayResponse.class);

            response.setCode(BusiConstant.PAY_REQUEST_SUCCESS);
//            response.setMsg(jsonRet.toString());
            com.alibaba.fastjson.JSONObject resResult = new com.alibaba.fastjson.JSONObject();
            resResult.put("qrcode",jsonRet.getString("codeUrl"));
            resResult.put("out_trade_no",payPaymentNo);
            response.setMsg(resResult.toJSONString());

        } else {
            response.setCode(BusiConstant.PAY_REQUEST_FAIL);
            response.setMsg(jsonRet.getString("desc"));
            //加下邮件预警
            PayBaseService.monitor(PayChannel.QFPAY.getName(), "钱方返回:" + jsonRet.getString("result") + jsonRet.getString("desc"),payPaymentNo);
        }

        logger.info("请求民生支付,支付流水," + payPaymentNo + "返回:" + JSON.toJSONString(response));
        return response;
    }
}
