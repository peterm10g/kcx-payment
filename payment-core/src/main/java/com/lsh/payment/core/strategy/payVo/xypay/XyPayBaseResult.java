package com.lsh.payment.core.strategy.payVo.xypay;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.lsh.payment.api.model.baseVo.ExceptionStatus;
import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.strategy.config.XYPayConfig;
import com.lsh.payment.core.util.pay.weChatpay.SignUtils;
import com.lsh.payment.core.util.pay.weChatpay.ToStringUtils;
import com.lsh.payment.core.util.pay.weChatpay.XStreamInitializer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * <pre>
 * 微信支付结果共用属性类
 * Created by Binary Wang on 2016-10-24.
 * @author <a href="https://github.com/binarywang">binarywang(Binary Wang)</a>
 * </pre>
 */
public abstract class XyPayBaseResult {


  //  @XStreamAlias("service")
  protected String service;

  protected String version;

  protected String charset;

  /**
   * 返回状态码
   */
//  @XStreamAlias("return_code")
  protected String status;
  /**
   * 返回信息
   */
//  @XStreamAlias("return_msg")
  protected String message;

  //当return_code为SUCCESS的时候，还会包括以下字段：

  /**
   * 业务结果
   */
  @XStreamAlias("result_code")
  private String resultCode;
  /**
   * 错误代码
   */
  @XStreamAlias("err_code")
  private String errCode;
  /**
   * 错误代码描述
   */
  @XStreamAlias("err_msg")
  private String errMsg;
  /**
   * 公众账号ID
   */
  @XStreamAlias("appid")
  private String appid;
  /**
   * 商户号
   */
  @XStreamAlias("mch_id")
  private String mchId;
  /**
   * 服务商模式下的子公众账号ID
   */
  @XStreamAlias("sub_appid")
  private String subAppId;
  /**
   * 服务商模式下的子商户号
   */
  @XStreamAlias("sub_mch_id")
  private String subMchId;
  /**
   * 随机字符串
   */
  @XStreamAlias("nonce_str")
  private String nonceStr;
  /**
   * 签名
   */
  @XStreamAlias("sign")
  private String sign;

  //以下为辅助属性
  /**
   * xml字符串
   */
  private String xmlString;

  /**
   * xml的Document对象，用于解析xml文本
   */
  private Document xmlDoc;

  /**
   * 将单位分转换成单位圆
   *
   * @param fee 将要被转换为元的分的数值
   */
  public static String feeToYuan(Integer fee) {
    return new BigDecimal(Double.valueOf(fee) / 100).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
  }

  /**
   * 从xml字符串创建bean对象
   */
  public static <T extends XyPayBaseResult> T fromXML(String xmlString, Class<T> clz) {
    XStream xstream = XStreamInitializer.getInstance();
    xstream.processAnnotations(clz);
    T result = (T) xstream.fromXML(xmlString);
    result.setXmlString(xmlString);
    return result;
  }

  public String getXmlString() {
    return this.xmlString;
  }

  public void setXmlString(String xmlString) {
    this.xmlString = xmlString;
  }

  protected Logger getLogger() {
    return LoggerFactory.getLogger(this.getClass());
  }

  @Override
  public String toString() {
    return ToStringUtils.toSimpleString(this);
  }

  public String getResultCode() {
    return this.resultCode;
  }

  public void setResultCode(String resultCode) {
    this.resultCode = resultCode;
  }

  public String getErrCode() {
    return this.errCode;
  }

  public void setErrCode(String errCode) {
    this.errCode = errCode;
  }

  public String getAppid() {
    return this.appid;
  }

  public void setAppid(String appid) {
    this.appid = appid;
  }

  public String getMchId() {
    return this.mchId;
  }

  public void setMchId(String mchId) {
    this.mchId = mchId;
  }

  public String getNonceStr() {
    return this.nonceStr;
  }

  public void setNonceStr(String nonceStr) {
    this.nonceStr = nonceStr;
  }

  public String getSign() {
    return this.sign;
  }

  public void setSign(String sign) {
    this.sign = sign;
  }

  public String getSubAppId() {
    return subAppId;
  }

  public void setSubAppId(String subAppId) {
    this.subAppId = subAppId;
  }

  public String getSubMchId() {
    return subMchId;
  }

  public void setSubMchId(String subMchId) {
    this.subMchId = subMchId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getErrMsg() {
    return errMsg;
  }

  public void setErrMsg(String errMsg) {
    this.errMsg = errMsg;
  }

  public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getCharset() {
    return charset;
  }

  public void setCharset(String charset) {
    this.charset = charset;
  }

  /**
   * 将bean通过保存的xml字符串转换成map
   */
  public Map<String, String> toMap() {
    if (StringUtils.isBlank(this.xmlString)) {
      throw new RuntimeException("xml数据有问题，请核实！");
    }

    Map<String, String> result = Maps.newHashMap();
    Document doc = this.getXmlDoc();

    try {
      NodeList list = (NodeList) XPathFactory.newInstance().newXPath()
        .compile("/xml/*")
        .evaluate(doc, XPathConstants.NODESET);
      int len = list.getLength();
      for (int i = 0; i < len; i++) {
        result.put(list.item(i).getNodeName(), list.item(i).getTextContent());
      }
    } catch (XPathExpressionException e) {
      throw new RuntimeException("非法的xml文本内容：" + xmlString);
    }

    return result;
  }

  /**
   * 将xml字符串转换成Document对象，以便读取其元素值
   */
  protected Document getXmlDoc() {
    if (this.xmlDoc != null) {
      return this.xmlDoc;
    }

    try {
      this.xmlDoc = DocumentBuilderFactory
        .newInstance()
        .newDocumentBuilder()
        .parse(new ByteArrayInputStream(this.xmlString.getBytes("UTF-8")));
      return xmlDoc;
    } catch (SAXException | IOException | ParserConfigurationException e) {
      throw new RuntimeException("非法的xml文本内容：" + this.xmlString);
    }

  }

  /**
   * 获取xml中元素的值
   */
  protected String getXmlValue(String... path) {
    Document doc = this.getXmlDoc();
    String expression = String.format("/%s//text()", Joiner.on("/").join(path));
    try {
      return (String) XPathFactory
        .newInstance()
        .newXPath()
        .compile(expression)
        .evaluate(doc, XPathConstants.STRING);
    } catch (XPathExpressionException e) {
      throw new RuntimeException("未找到相应路径的文本：" + expression);
    }
  }

  /**
   * 获取xml中元素的值，作为int值返回
   */
  protected Integer getXmlValueAsInt(String... path) {
    String result = this.getXmlValue(path);
    if (StringUtils.isBlank(result)) {
      return null;
    }

    return Integer.valueOf(result);
  }


  /**
   * 校验返回结果签名
   */
  public void checkResult() {
    //校验返回结果签名
    Map<String, String> map = toMap();
    if (getSign() != null && !SignUtils.checkSign(map, XYPayConfig.KEY)) {
      this.getLogger().debug("校验结果签名失败，参数：{}", map);
      throw new BusinessException(ExceptionStatus.E2001005.getCode(), ExceptionStatus.E2001005.getMessage());
    }

    //校验结果是否成功
    if (!"0".equalsIgnoreCase(getStatus()) || !"0".equalsIgnoreCase(getResultCode())) {
      StringBuilder errorMsg = new StringBuilder();
      if (getStatus() != null) {
        errorMsg.append("返回代码：").append(getStatus());
      }
      if (getMessage() != null) {
        errorMsg.append("，返回信息：").append(getMessage());
      }
      if (getResultCode() != null) {
        errorMsg.append("，结果代码：").append(getResultCode());
      }
      if (getErrCode() != null) {
        errorMsg.append("，错误代码：").append(getErrCode());
      }
      if (getErrMsg() != null) {
        errorMsg.append("，错误详情：").append(getErrMsg());
      }

      this.getLogger().error("\n结果业务代码异常，返回結果：{},\n{}", map, errorMsg.toString());

//      throw new BusinessException(ExceptionStatus.E2001005.getCode(), errorMsg.toString() + ExceptionStatus.E2001005.getMessage());
    }

  }



}
