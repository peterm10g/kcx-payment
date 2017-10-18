package com.lsh.payment.core.strategy.queryStrategy;

import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.strategy.config.AllinPayConfig;
import com.lsh.payment.core.strategy.config.CMBCPayConfig;
import com.lsh.payment.core.strategy.payVo.allinpay.AllinQueryResponse;
import com.lsh.payment.core.strategy.payVo.cmbcpay.OrderQueryReq;
import com.lsh.payment.core.util.PayAssert;
import com.lsh.payment.core.util.pay.allinpay.HttpConnectionUtil;
import com.lsh.payment.core.util.pay.allinpay.SybConstants;
import com.lsh.payment.core.util.pay.allinpay.SybPayService;
import com.lsh.payment.core.util.pay.allinpay.SybUtil;
import com.lsh.payment.core.util.pay.cmbcpay.Md5SignUtil;
import com.lsh.payment.core.util.pay.cmbcpay.ObjectAndMapConvert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/11/6.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class AllinQueryStrategy implements QueryStrategy<AllinQueryResponse> {

    private Logger logger = LoggerFactory.getLogger(AllinQueryStrategy.class);

    /**
     * 查询第三方支付状态
     *
     * @param paramMap 查询参数
     * @return
     */
    @Override
    public AllinQueryResponse queryPayStatusByParams(Map<String, Object> paramMap) {

        logger.info("民生查询:" + JSON.toJSONString(paramMap));

        OrderQueryReq orderQueryReq = new OrderQueryReq();
        orderQueryReq.setMerNo(CMBCPayConfig.MAC_ID);
        orderQueryReq.setOrgTransId(paramMap.get("channelId").toString());

        orderQueryReq.setSignIn(Md5SignUtil.md5Sign(ObjectAndMapConvert.convertObjectToMap(orderQueryReq), CMBCPayConfig.KEY));
        Map<String,String> resMap = null;
        try {
            TreeMap<String,String> params = new TreeMap<String,String>();
            params.put("cusid", AllinPayConfig.CUS_ID);
            params.put("appid", AllinPayConfig.APP_ID);
            params.put("version", "11");
//            params.put("reqsn", reqsn);
            params.put("trxid", paramMap.get("channelId").toString());
            params.put("randomstr", SybUtil.getValidatecode(8));
            params.put("sign", SybUtil.sign(params,AllinPayConfig.KEY));

            HttpConnectionUtil http = new HttpConnectionUtil(SybConstants.SYB_APIURL+"/query");


            byte[] bys = http.postParams(params, true);
            String result = new String(bys,"UTF-8");
            resMap = SybPayService.handleResult(result);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        AllinQueryResponse qfQueryRresponse = this.buildRsp(resMap);

        logger.info("请求钱方支付,第三方支付流水," + paramMap.get("channelId") + "返回:" + JSON.toJSONString(qfQueryRresponse));

        return qfQueryRresponse;
    }

    /**
     * @param resMap
     * @return
     */
    private AllinQueryResponse buildRsp(Map<String, String> resMap) {

        AllinQueryResponse allinQueryResponse = new AllinQueryResponse();
        allinQueryResponse.setCode(BusiConstant.PAY_REQUEST_FAIL);
        allinQueryResponse.setMsg("通联支付通信失败");
        //校验返回参数
        if (resMap.get("retcode").equals("SUCCESS") && resMap.get("trxstatus").equals(AllinPayConfig.PAY_STATE_SUCCESS)) {

            allinQueryResponse.setCode(BusiConstant.PAY_REQUEST_SUCCESS);
            allinQueryResponse.setRetcode(resMap.get("retcode"));
            allinQueryResponse.setRetmsg(resMap.get("retmsg"));
            allinQueryResponse.setCusid(resMap.get("cusid"));
            allinQueryResponse.setAppid(resMap.get("appid"));
            allinQueryResponse.setTrxid(resMap.get("trxid"));
            allinQueryResponse.setChnltrxid(resMap.get("chnltrxid"));
            allinQueryResponse.setReqsn(resMap.get("reqsn"));
            allinQueryResponse.setTrxcode(resMap.get("trxcode"));
            allinQueryResponse.setTrxamt(resMap.get("trxamt"));
            allinQueryResponse.setFintime(resMap.get("fintime"));
            allinQueryResponse.setRandomstr(resMap.get("randomstr"));
            allinQueryResponse.setErrmsg(resMap.get("errmsg"));
            allinQueryResponse.setSign(resMap.get("sign"));

            allinQueryResponse.setMsg("通联签名校验或通信成功");
        } else {

            logger.info("民生返回查询数据有误");
        }

        return allinQueryResponse;
    }


    private boolean checkPass(Map<String, String> resMap,AllinQueryResponse cmbcQueryResponse) {
        String sign = Md5SignUtil.md5Sign(ObjectAndMapConvert.removeEmptyData(resMap), CMBCPayConfig.KEY);

        PayAssert.notNull(cmbcQueryResponse, ExceptionStatus.E1002002.getCode(),ExceptionStatus.E1002002.getMessage());

        PayAssert.notNull(cmbcQueryResponse.getRetcode(),ExceptionStatus.E1002002.getCode(),ExceptionStatus.E1002002.getMessage());

        PayAssert.notNull(cmbcQueryResponse.getTrxstatus(),ExceptionStatus.E1002002.getCode(),ExceptionStatus.E1002002.getMessage());

        return sign.equals(resMap.get("signOut"));
    }

}
