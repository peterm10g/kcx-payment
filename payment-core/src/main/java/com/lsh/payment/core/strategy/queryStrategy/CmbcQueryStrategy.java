package com.lsh.payment.core.strategy.queryStrategy;

import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.strategy.config.CMBCPayConfig;
import com.lsh.payment.core.strategy.payVo.cmbcpay.CmbcQueryResponse;
import com.lsh.payment.core.strategy.payVo.cmbcpay.OrderQueryReq;
import com.lsh.payment.core.util.PayAssert;
import com.lsh.payment.core.util.pay.cmbcpay.HttpPostRequest;
import com.lsh.payment.core.util.pay.cmbcpay.Md5SignUtil;
import com.lsh.payment.core.util.pay.cmbcpay.ObjectAndMapConvert;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/6.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class CmbcQueryStrategy implements QueryStrategy<CmbcQueryResponse> {

    private Logger logger = LoggerFactory.getLogger(CmbcQueryStrategy.class);

    /**
     * 查询第三方支付状态
     *
     * @param paramMap 查询参数
     * @return
     */
    @Override
    public CmbcQueryResponse queryPayStatusByParams(Map<String, Object> paramMap) {

        logger.info("民生查询:" + JSON.toJSONString(paramMap));

        OrderQueryReq orderQueryReq = new OrderQueryReq();
        orderQueryReq.setMerNo(CMBCPayConfig.MAC_ID);
        orderQueryReq.setOrgTransId(paramMap.get("channelId").toString());

        orderQueryReq.setSignIn(Md5SignUtil.md5Sign(ObjectAndMapConvert.convertObjectToMap(orderQueryReq), CMBCPayConfig.KEY));

        JSONObject jsonRet = HttpPostRequest.sendRequest(ObjectAndMapConvert.convertObjectToMap(orderQueryReq), CMBCPayConfig.PAY_API + CMBCPayConfig.PAY_QUERY);

        Map<String, String> resMap = JSON.parseObject(jsonRet.toString(), Map.class);

        CmbcQueryResponse qfQueryRresponse = this.buildRsp(resMap, jsonRet);

        logger.info("请求钱方支付,第三方支付流水," + paramMap.get("channelId") + "返回:" + JSON.toJSONString(qfQueryRresponse));

        return qfQueryRresponse;
    }

    /**
     * @param resMap
     * @return
     */
    private CmbcQueryResponse buildRsp(Map<String, String> resMap, JSONObject jsonRet) {

        CmbcQueryResponse cmbcQueryResponse = (CmbcQueryResponse) JSONObject.toBean(jsonRet, CmbcQueryResponse.class);
        cmbcQueryResponse.setCode(BusiConstant.PAY_REQUEST_FAIL);
        cmbcQueryResponse.setMsg("钱方支付通信失败");
        if (!this.checkPass(resMap,cmbcQueryResponse)) {
            logger.info("民生签名校验失败,{}", jsonRet);
            return cmbcQueryResponse;
        }
        //校验返回参数
        if (cmbcQueryResponse.getResult().equals(CMBCPayConfig.TRADE_STATE_SUCCESS) && cmbcQueryResponse.getTransStatus().equals(CMBCPayConfig.PAY_STATE_SUCCESS)) {

            cmbcQueryResponse.setCode(BusiConstant.PAY_REQUEST_SUCCESS);
            cmbcQueryResponse.setMsg("钱方签名校验或通信成功");
        } else {

            logger.info("民生返回查询数据有误");
        }

        return cmbcQueryResponse;
    }


    private boolean checkPass(Map<String, String> resMap,CmbcQueryResponse cmbcQueryResponse) {
        String sign = Md5SignUtil.md5Sign(ObjectAndMapConvert.removeEmptyData(resMap), CMBCPayConfig.KEY);

        PayAssert.notNull(cmbcQueryResponse, ExceptionStatus.E1002002.getCode(),ExceptionStatus.E1002002.getMessage());

        PayAssert.notNull(cmbcQueryResponse.getResult(),ExceptionStatus.E1002002.getCode(),ExceptionStatus.E1002002.getMessage());

        PayAssert.notNull(cmbcQueryResponse.getSignOut(),ExceptionStatus.E1002002.getCode(),ExceptionStatus.E1002002.getMessage());

        return sign.equals(resMap.get("signOut"));
    }

}
