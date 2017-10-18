package com.lsh.payment.core.strategy.queryStrategy;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.strategy.config.AliPayConfig;
import com.lsh.payment.core.strategy.payVo.alipay.AliQueryResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/6.
 * 北京链商电子商务有限公司
 * desc:
 */
public class AliQueryStrategy implements QueryStrategy<AliQueryResponse> {

    private Logger logger = LoggerFactory.getLogger(AliQueryStrategy.class);

    private AlipayClient alipayClient = new DefaultAlipayClient(AliPayConfig.ALIPAY_QUERY_API, AliPayConfig.app_id, AliPayConfig.private_key, "json", "UTF-8", AliPayConfig.alipay_public_key);

    /**
     * 查询第三方支付状态
     *
     * @param paramMap 查询参数
     * @return
     */
    @Override
    public AliQueryResponse queryPayStatusByParams(Map<String, Object> paramMap) {

        AliQueryResponse aliQueryResponse = new AliQueryResponse();

        AlipayTradeQueryRequest tradeQueryRequest = new AlipayTradeQueryRequest();

        Map<String, Object> jsonMap = new HashMap<>(4);
        jsonMap.put("out_trade_no", paramMap.get("out_trade_no"));
        jsonMap.put("trade_no", paramMap.get("channelId"));

        tradeQueryRequest.setBizContent(JSON.toJSONString(jsonMap));

        AlipayTradeQueryResponse tradeQueryResponse;
        aliQueryResponse.setCode(BusiConstant.PAY_REQUEST_FAIL);
        aliQueryResponse.setMsg("查询支付宝支付结果请求失败");
        try {

            logger.info("查询请求支付宝参数:" + JSON.toJSONString(tradeQueryRequest));
            tradeQueryResponse = alipayClient.execute(tradeQueryRequest);
            logger.info("查询请求支付宝返回:" + JSON.toJSONString(tradeQueryResponse));

            if (tradeQueryResponse != null && StringUtils.isNotBlank(tradeQueryResponse.getCode()) && tradeQueryResponse.getCode().equals(AliPayConfig.SUCCESS_CODE)) {

                aliQueryResponse.setResp(tradeQueryResponse);
                aliQueryResponse.setCode(BusiConstant.PAY_REQUEST_SUCCESS);
                aliQueryResponse.setMsg("查询支付宝支付结果成功");
            } else {
                logger.info("查询支付宝支付结果失败 : AlipayTradeQueryResponse is : " + JSON.toJSONString(tradeQueryResponse));
            }
            logger.info("支付宝," + paramMap.get("out_trade_no") + ",查询返回:" + JSON.toJSONString(aliQueryResponse));

        } catch (AlipayApiException e) {
            logger.error("支付宝查询失败, 支付流水: " + paramMap.get("out_trade_no") +
                    ", 支付宝支付流水: " + paramMap.get("channelId"), e);
        } catch (Throwable e) {
            logger.error("请求支付宝未知异常", e);
        }

        return aliQueryResponse;
    }
}
