package com.lsh.payment.core.strategy.payStrategy;

import com.alibaba.fastjson.JSON;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.model.payEnum.AllinPayType;
import com.lsh.payment.core.model.payEnum.TradeType;
import com.lsh.payment.core.strategy.config.AllinPayConfig;
import com.lsh.payment.core.strategy.config.CMBCPayConfig;
import com.lsh.payment.core.strategy.config.QFPayConfig;
import com.lsh.payment.core.strategy.payVo.allinpay.AllinpayResponse;
import com.lsh.payment.core.util.BigDecimalUtils;
import com.lsh.payment.core.util.pay.allinpay.HttpConnectionUtil;
import com.lsh.payment.core.util.pay.allinpay.SybConstants;
import com.lsh.payment.core.util.pay.allinpay.SybPayService;
import com.lsh.payment.core.util.pay.allinpay.SybUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;


/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/23.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class AllinPayStrategy implements PayStrategy<AllinpayResponse> {

    private Logger logger = LoggerFactory.getLogger(AllinPayStrategy.class);

    /**
     * 调用对应支付平台组装支付请求报文
     *
     * @param payType  传入需要的支付方式
     * @param paramMap 其他额外需要的参数
     * @return 生成的支付请求
     */
    @Override
    public AllinpayResponse generatePayParams(int payType, final Map<String, String> paramMap) {
        String prefix = "请求民生下单 ";
        logger.info("{}参数: {}", prefix, paramMap);
        BigDecimal amount = BigDecimalUtils.yuanToFen(paramMap.get("payAmount"));
        String payPaymentNo = paramMap.get("payPaymentNo");
        int channelType = Integer.parseInt(paramMap.get("channelType"));
        //组装报文

        TreeMap<String,String> params = new TreeMap<>();
        params.put("cusid", AllinPayConfig.CUS_ID);
        params.put("appid", AllinPayConfig.APP_ID);
        params.put("version", "11");
        params.put("trxamt", amount.toString());
        params.put("reqsn", payPaymentNo);

        if (channelType == TradeType.ALLINWXSM.getCode()) {
            params.put("paytype", AllinPayType.WXSM.getCode());
        } else if (channelType == TradeType.ALLINALISM.getCode()) {
            params.put("paytype", AllinPayType.ALISM.getCode());
        }

        params.put("randomstr", SybUtil.getValidatecode(8));
        params.put("body", QFPayConfig.GOODS_NAME);
//        params.put("remark", remark);
//        params.put("acct", acct);
//        params.put("authcode", authcode);
        params.put("notify_url", AllinPayConfig.NOTIFY_URL);
//        params.put("limit_pay", limit_pay);


        HttpConnectionUtil http = new HttpConnectionUtil(SybConstants.SYB_APIURL+"/pay");
        Map<String,String> map = null;
        String result = null;
        try {
            http.init();

            params.put("sign", SybUtil.sign(params,AllinPayConfig.KEY));

            logger.info("{} 下单参数 {}", prefix,JSON.toJSONString(params));

            byte[] bys = http.postParams(params, true);
            result = new String(bys,"UTF-8");

            map = SybPayService.handleResult(result);

            logger.info(result);
        } catch (Exception e) {
            e.printStackTrace();

        }


//        String callRsp = HttpClientUtils.doPostStr(AllinPayConfig.PAY_BASE + AllinPayConfig.PAY_NATIVE, params);

//        JSONObject jsonRet = HttpPostRequest.sendRequest(ObjectAndMapConvert.convertObjectToMap(req), CMBCPayConfig.PAY_API + CMBCPayConfig.PAY_NATIVE);
//        {"codeUrl":"https://qr.alipay.com/bax00793wbl6ibbnskap00ad","desc":"下单成功","orderNo":"905702283080105984","reqId":"905702283080105984","result":"0000","signOut":"EE12D7319884E392EC2116D534D163C0","transId":"20170907155748994732"}

        return this.buildRsp(map,result,payPaymentNo);
    }




    /**
     * 民生下单返回值
     *
     * @param resMap 返回结果
     * @return 支付平台返回结果
     */
    private AllinpayResponse buildRsp(Map<String,String> resMap,String resultStr ,String payPaymentNo) {

        AllinpayResponse response = new AllinpayResponse();
        response.setCode(BusiConstant.PAY_REQUEST_FAIL);
        response.setMsg(BusiConstant.PAY_REQUEST_FAIL_MESSAGE);

        logger.info("支付流水号 {} callRsp = {}", payPaymentNo, JSON.toJSONString(resMap));

        String result = resMap.get("trxstatus");
        if (StringUtils.isNotBlank(result) && result.equals(CMBCPayConfig.TRADE_STATE_SUCCESS)) {
            response = JSON.parseObject(resultStr.toString(), AllinpayResponse.class);

            response.setCode(BusiConstant.PAY_REQUEST_SUCCESS);
//            response.setMsg(jsonRet.toString());
            com.alibaba.fastjson.JSONObject resResult = new com.alibaba.fastjson.JSONObject();
            resResult.put("qrcode",response.getPayinfo());
            resResult.put("out_trade_no",payPaymentNo);
            response.setMsg(resResult.toJSONString());

        } else {
            response.setCode(BusiConstant.PAY_REQUEST_FAIL);
            response.setMsg(resMap.get("errmsg"));
            //加下邮件预警
//            PayBaseService.monitor(PayChannel.QFPAY.getName(), "钱方返回:" + resMap.get("result") + jsonRet.getString("desc"),payPaymentNo);
        }

        logger.info("请求通联支付,支付流水," + payPaymentNo + "返回:" + JSON.toJSONString(response));
        return response;
    }
}
