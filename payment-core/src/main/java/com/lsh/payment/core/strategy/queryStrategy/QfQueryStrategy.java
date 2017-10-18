package com.lsh.payment.core.strategy.queryStrategy;

import com.alibaba.fastjson.JSON;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.strategy.payVo.qfpay.QFData;
import com.lsh.payment.core.strategy.config.QFPayConfig;
import com.lsh.payment.core.strategy.payVo.qfpay.QFQueryResponse;
import com.lsh.payment.core.util.pay.qfpay.QFSignature;
import com.lsh.payment.core.util.HttpClientUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
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
public class QfQueryStrategy implements QueryStrategy<QFQueryResponse> {

    private Logger logger = LoggerFactory.getLogger(QfQueryStrategy.class);

    /**
     * 查询第三方支付状态
     *
     * @param paramMap 查询参数
     * @return
     */
    @Override
    public QFQueryResponse queryPayStatusByParams(Map<String, Object> paramMap) {

        logger.info("查询钱方:" + JSON.toJSONString(paramMap));
        Map<String, Object> qfQueryBody = new HashMap<>(2);
        qfQueryBody.put("syssn", paramMap.get("channelId"));
        //签名
        String sign = QFSignature.getSign(qfQueryBody);
        //报文头
        Map<String, String> qfQueryHead = new HashMap<>(4);
        qfQueryHead.put("X-QF-APPCODE", QFPayConfig.APP_CODE);
        qfQueryHead.put("X-QF-SIGN", sign);
        //请求钱方
        Map<String, String> qfResponseMap = HttpClientUtils.doPostForQF(QFPayConfig.CHECK_API, qfQueryBody, qfQueryHead);

        QFQueryResponse qfQueryRresponse = this.mkRsp(qfResponseMap);

        logger.info("请求钱方支付,第三方支付流水," + paramMap.get("channelId") + "返回:" + JSON.toJSONString(qfQueryRresponse));

        return qfQueryRresponse;

    }

    /**
     * @param callRsp
     * @return
     */
    private QFQueryResponse mkRsp(Map<String, String> callRsp) {
        QFQueryResponse qfQueryResponse = new QFQueryResponse();
        qfQueryResponse.setCode(BusiConstant.PAY_REQUEST_FAIL);
        qfQueryResponse.setMsg("钱方支付通信失败");

        String rspBody = callRsp.get("httpStr");
        String rspSign = callRsp.get("sign");
        if (QFSignature.check(rspBody, rspSign)) {

            JSONObject qfResponseJson = JSONObject.fromObject(rspBody);
            Map<String, Class> classMap = new HashMap<>(2);
            classMap.put("data", QFData.class);
            qfQueryResponse = (QFQueryResponse) JSONObject.toBean(qfResponseJson, QFQueryResponse.class, classMap);
            //校验返回参数
            if (StringUtils.isNotBlank(qfQueryResponse.getRespcd()) && qfQueryResponse.getRespcd().equals(QFPayConfig.RESPCD_SUCCESS)) {//通信成功

                qfQueryResponse.setCode(BusiConstant.PAY_REQUEST_SUCCESS);
                qfQueryResponse.setMsg("钱方签名校验或通信成功");
            } else {
                logger.info("钱方返回查询数据有误");
            }

        } else {
            logger.info("钱方签名校验失败");
        }

        return qfQueryResponse;
    }

}
