package com.lsh.payment.core.util;

import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

public class XmlUtil {
    private static final Log logger = LogFactory.getLog(XmlUtil.class);

    public static JSONObject parseXmlJson(String xml) {
        try {
            SAXReader builder = new SAXReader();
            Document document = builder.read(new StringReader(xml));
            Element root = document.getRootElement();
            List<Element> elements = root.elements();
            JSONObject json = new JSONObject();

            for (Element e : elements) {
                if (e.isTextOnly()) {
                    json.put(e.getName(), e.getText());
                } else {
                    json.put(e.getName(), parseXmlJson(e));
                }
            }
            return json;
        } catch (Exception e) {
            logger.error(e.getCause());
            return null;
        }
    }

    public static JSONObject parseXmlJson(Element root) {
        try {
            List<Element> elements = root.elements();
            JSONObject json = new JSONObject();
            for (Element e : elements) {
                if (e.isTextOnly()) {
                    json.put(e.getName(), e.getText());
                } else {
                    json.put(e.getName(), parseXmlJson(e));
                }
            }
            return json;
        } catch (Exception e) {
            logger.error(e.getCause());
            return null;
        }
    }


    public static String toXml(Map<String, String> data) {
        Document document = DocumentHelper.createDocument();
        Element nodeElement = document.addElement("xml");

        for (Map.Entry<String,String> entry : data.entrySet()) {
            Element keyElement = nodeElement.addElement(entry.getKey());
            keyElement.setText(entry.getValue());
        }
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            OutputFormat format = new OutputFormat("   ", true, "UTF-8");
            XMLWriter writer = new XMLWriter(out, format);
            writer.write(document);
            return out.toString("UTF-8");
        } catch (Exception ex) {

            logger.error(ex.getCause());
        }
        return null;
    }


}
