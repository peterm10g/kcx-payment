package com.lsh.payment.core.strategy.queryStrategy;

import com.alibaba.fastjson.JSON;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.strategy.config.XYPayConfig;
import com.lsh.payment.core.strategy.payVo.xypay.XyPayBaseResult;
import com.lsh.payment.core.strategy.payVo.xypay.XyPayOrderQueryResult;
import com.lsh.payment.core.strategy.payVo.xypay.XyQueryResponse;
import com.lsh.payment.core.util.HttpClientUtils;
import com.lsh.payment.core.util.MD5Util;
import com.lsh.payment.core.util.XmlUtil;
import com.lsh.payment.core.util.pay.weChatpay.WxSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/6.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class XyQueryStrategy implements QueryStrategy<XyQueryResponse> {

    private Logger logger = LoggerFactory.getLogger(XyQueryStrategy.class);

    /**
     * 查询第三方支付状态
     *
     * @param paramMap 查询参数
     * @return
     */
    @Override
    public XyQueryResponse queryPayStatusByParams(Map<String, Object> paramMap) {

        logger.info("查询钱方:" + JSON.toJSONString(paramMap));
        Map<String, String> xyQueryBody = new HashMap<>(8);
        xyQueryBody.put("out_trade_no", paramMap.get("payPaymentNo").toString());
        xyQueryBody.put("service", XYPayConfig.XY_QUERY);
        xyQueryBody.put("mch_id", XYPayConfig.MAC_ID);
        xyQueryBody.put("nonce_str", MD5Util.generateNonceStr());// 随机字符串
        xyQueryBody.put("sign", WxSignature.getNewSign(xyQueryBody, XYPayConfig.KEY));

        String queryXml = XmlUtil.toXml(xyQueryBody);


        //请求钱方
        String xyPayOrderQueryResultXml = HttpClientUtils.doPostXml(XYPayConfig.PAY_API, queryXml);

        XyPayOrderQueryResult xyPayOrderQueryResult = XyPayBaseResult.fromXML(xyPayOrderQueryResultXml, XyPayOrderQueryResult.class);

        xyPayOrderQueryResult.checkResult();

        return this.buildRsp(xyPayOrderQueryResult);
    }

    /**
     * @param xyPayOrderQueryResult
     * @return
     */
    private XyQueryResponse buildRsp(XyPayOrderQueryResult xyPayOrderQueryResult) {
        XyQueryResponse qfQueryResponse = new XyQueryResponse();
        qfQueryResponse.setCode(BusiConstant.PAY_REQUEST_FAIL);
        qfQueryResponse.setMsg("兴业支付通信失败");

        //校验返回参数
        if (xyPayOrderQueryResult.getStatus().equals("0") && xyPayOrderQueryResult.getResultCode().equals("0")) {//通信成功

            qfQueryResponse.setCode(BusiConstant.PAY_REQUEST_SUCCESS);
            qfQueryResponse.setMsg("签名校验或通信成功");
            qfQueryResponse.setXyPayOrderQueryResult(xyPayOrderQueryResult);
        } else {
            logger.info("返回查询数据有误");
        }

        return qfQueryResponse;
    }

}
