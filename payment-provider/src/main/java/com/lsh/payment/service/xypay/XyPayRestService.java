package com.lsh.payment.service.xypay;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;
import com.alibaba.fastjson.JSON;
import com.lsh.payment.api.service.xypay.IXyPayRestService;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.service.xypay.impl.XyPayNotifyService;
import com.lsh.payment.core.strategy.config.XYPayConfig;
import com.lsh.payment.core.util.XmlUtil;
import com.lsh.payment.core.util.pay.weChatpay.SignUtils;
import com.lsh.payment.core.util.pay.weChatpay.WxSignature;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
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
@Path("xypay")
public class XyPayRestService implements IXyPayRestService {

    private static Logger logger = LoggerFactory.getLogger(XyPayRestService.class);

    @Autowired
    private XyPayNotifyService xyPayNotifyService;

    /**
     * 微信回调
     *
     * @param request request
     * @return WeChatNotifyResponse
     */
    @POST
    @Path("notify")
    @Produces({ContentType.TEXT_XML_UTF_8})
    public String xyNotify(@Context HttpServletRequest request) {

        String wxNotifyResponse = "";

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;

        long beginTime = System.currentTimeMillis();
        try {

            documentBuilder = dbf.newDocumentBuilder();

            Document doc = documentBuilder.parse(request.getInputStream());
            String params = toFormatedXML(doc);
            JSONObject jsonObject = XmlUtil.parseXmlJson(params);
            Map<String, String> paramsMap = (Map) JSONObject.toBean(jsonObject, HashMap.class);

            logger.info("xypay notify data is {}", JSON.toJSONString(paramsMap));

            if (SignUtils.checkSign(paramsMap,XYPayConfig.KEY)) {

                long t1 = System.currentTimeMillis();
                xyPayNotifyService.dealNotify(paramsMap);


                long t2 = System.currentTimeMillis();
                logger.info("wxpay notify time is {}", (t2 - t1));

                wxNotifyResponse = "success";
            } else {
                logger.info("xypay notify checkSign is fail.");
                wxNotifyResponse = "fail";
            }
        } catch (BusinessException e) {

            wxNotifyResponse = "fail";
            logger.error("业务异常:" + e.getCode() + e.getMessage());
        } catch (Throwable e) {

            wxNotifyResponse = "fail";
            logger.error("服务端异常", e);
        }

        return wxNotifyResponse;
    }

    /**
     * @param params 签名参数
     * @return boolean
     */
    private boolean checkSign(Map<String, String> params) {
       logger.info("wx params is " + JSON.toJSONString(params));

        String sign = params.get("sign").toString().trim();

        String checkSign = WxSignature.getNewSign(params, XYPayConfig.KEY);
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




}
