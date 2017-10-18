package com.lsh.payment.service.wxPay;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;
import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.model.baseVo.BaseResponse;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.api.model.wxpay.WxNotifyResponse;
import com.lsh.payment.api.service.weChatPay.IWeChatPayRestService;
import com.lsh.payment.core.constant.BusiConstant;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.model.Async.PayMonitorInterfaceModel;
import com.lsh.payment.core.model.payEnum.BillTradeType;
import com.lsh.payment.core.service.AsyncService.AsyncEvent;
import com.lsh.payment.core.service.wxPay.impl.WxNotifyServiceImpl;
import com.lsh.payment.core.strategy.config.WxPayConfig;
import com.lsh.payment.core.util.DateUtil;
import com.lsh.payment.core.util.JsApiPay;
import com.lsh.payment.core.util.XmlUtil;
import com.lsh.payment.core.util.pay.weChatpay.WxSignature;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/27.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service(protocol = "rest")
@Path("wxpay")
public class WxPayRestService implements IWeChatPayRestService {

    private static Logger logger = LoggerFactory.getLogger(WxPayRestService.class);

    private static Map<String,String> mac2keyMap;



    @Autowired
    private WxNotifyServiceImpl wxNotifyServiceImpl;

    static {
        mac2keyMap = new HashMap<>();
        mac2keyMap.put(WxPayConfig.MCHID,WxPayConfig.KEY);
        mac2keyMap.put(WxPayConfig.GROUPON_MCHID,WxPayConfig.GROUPON_KEY);
    }

    /**
     * 微信回调
     *
     * @param request request
     * @return WeChatNotifyResponse
     */
    @POST
    @Path("notify")
    @Produces({ContentType.TEXT_XML_UTF_8})
    public WxNotifyResponse weChatNotify(@Context HttpServletRequest request) {

        WxNotifyResponse wxNotifyResponse = new WxNotifyResponse();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;

        long beginTime = System.currentTimeMillis();
        try {

            documentBuilder = dbf.newDocumentBuilder();

            Document doc = documentBuilder.parse(request.getInputStream());
            String params = toFormatedXML(doc);
            JSONObject jsonObject = XmlUtil.parseXmlJson(params);
            Map<String, String> paramsMap = (Map) JSONObject.toBean(jsonObject, HashMap.class);

            logger.info("wxpay notify data is {}", JSON.toJSONString(paramsMap));

            if (this.checkSign(paramsMap,mac2keyMap)) {

                long t1 = System.currentTimeMillis();
                wxNotifyServiceImpl.weChatNotify(paramsMap);
                long t2 = System.currentTimeMillis();
                logger.info("wxpay notify time is {}", (t2 - t1));

                wxNotifyResponse.setReturn_code(BusiConstant.OPERATE_SUCCESS_STRING);
                wxNotifyResponse.setReturn_msg("OK");

            } else {
                logger.info("wxpay notify checkSign is fail.");
                wxNotifyResponse.setReturn_code(BusiConstant.OPERATE_FAIL_STRING);
                wxNotifyResponse.setReturn_msg("签名失败");
            }
        } catch (BusinessException e) {

            wxNotifyResponse.setReturn_code(ExceptionStatus.FAIL_S.getMessage());
            wxNotifyResponse.setReturn_msg(e.getMessage());
            logger.error("业务异常:" + e.getCode() + e.getMessage());
        } catch (Throwable e) {

            wxNotifyResponse.setReturn_code(ExceptionStatus.FAIL_S.getMessage());
            wxNotifyResponse.setReturn_msg("服务端异常");
            logger.error("服务端异常", e);
        }

        try {
            long wasteTime = System.currentTimeMillis() - beginTime;
            logger.info("微信回调接口耗时 {} 毫秒。", wasteTime);
            String wasteTimeStr = (new StringBuffer(DateUtil.nowStrFormate())).append("[").append(wasteTime).append("]").toString();
            PayMonitorInterfaceModel payMonitorInterfaceModel = new PayMonitorInterfaceModel(request, wasteTimeStr, this.getClass().getSimpleName());
            if (!BusiConstant.OPERATE_SUCCESS_STRING.equals(wxNotifyResponse.getReturn_code()))
                payMonitorInterfaceModel.setResultFlag(false);
            AsyncEvent.post(payMonitorInterfaceModel);
        } catch (Throwable e) {
            logger.error("收集监控信息失败", e);
        }
        return wxNotifyResponse;
    }

    /**
     * @param params 签名参数
     * @return boolean
     */
    private boolean checkSign(Map<String, String> params,final Map<String,String> mac2keyMap) {
       logger.info("wx params is " + JSON.toJSONString(params));

        String sign = params.get("sign").toString().trim();
        String macId = params.get("mch_id").toString().trim();

        String wxkey;
        if (StringUtils.isNotBlank(params.get("trade_type"))) {
            String tradeType = params.get("trade_type");
            if (tradeType.equals(BillTradeType.JSAPI.getName())) {
                wxkey = mac2keyMap.get(macId);
//                wxkey = WxPayConfig.KEY;
            } else if (tradeType.equals(BillTradeType.APP.getName())) {
                wxkey = WxPayConfig.KEY_APP;
            } else {
                logger.info("trade_type 字段错误 trade_type = " + params.get("trade_type"));
                return false;
            }
        } else {
            logger.info("trade_type 字段错误 trade_type = " + params.get("trade_type"));
            return false;
        }

        String checkSign = WxSignature.getNewSign(params, wxkey);

        return sign.equals(checkSign.trim());
    }

    /**
     * @param document xml
     * @return Exception
     * @throws Exception
     */
    private String toFormatedXML(Document document) throws Exception {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transFormer = transFactory.newTransformer();
        transFormer.setOutputProperty(OutputKeys.ENCODING, "GB2312");
        DOMSource domSource = new DOMSource(document);

        StringWriter sw = new StringWriter();
        StreamResult xmlResult = new StreamResult(sw);

        transFormer.transform(domSource, xmlResult);

        return sw.toString();

    }


    @GET
    @Path("setCode")
    @Produces({ContentType.APPLICATION_JSON_UTF_8})
    public BaseResponse getWxChatCode(@Context HttpServletRequest request) {

        try {

            String code = request.getParameter("code");
            String state = request.getParameter("state");

            logger.info("微信传回code参数 : code = " + code + " , state = " + state);

            if (StringUtils.isNotBlank(code) && StringUtils.isNotBlank(state)) {
                String openId = JsApiPay.getOpenId(code);
                logger.info("wx h5 openid is " + openId);

                return null;
            } else {
                logger.info("微信传回code参数异常 : code = " + code + " , state = " + state);
            }
        } catch (Exception e) {
            logger.error(" 获取openid异常: ", e);
        }

        return null;

    }

}
