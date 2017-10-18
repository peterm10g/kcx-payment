package com.lsh.payment.core.util;

import com.lsh.payment.core.service.payment.impl.PayBaseService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * HTTP 请求工具类
 *
 * @author : miaozhuang
 * @version : 1.0.0
 * @date : 2016/8/24
 * @see : TODO
 */
public class HttpClientUtils {

    private static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT = 30000;
    private static final int MAX_READ_TIMEOUT = 60000;

    static {
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager();
        // 设置连接池大小
        connMgr.setMaxTotal(100);

        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_READ_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
        //
        requestConfig = configBuilder.build();
    }

    /**
     * 发送 GET 请求（HTTP），不带输入数据
     *
     * @param url 请求地址
     * @return String
     */
    public static String doGet(String url) {

        return doGet(url, new HashMap<String, Object>());
    }

    /**
     * 发送 GET 请求（HTTP），不带输入数据
     *
     * @param url 请求URL
     * @return String
     */
    public static String doGet(String url, Map<String, Object> params) {

        return doGet(url, params, new HashMap<String, String>());
    }

    /**
     * 发送 GET 请求（HTTP），K-V形式
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return String
     */
    public static String doGet(String url, Map<String, Object> params, Map<String, String> headMap) {
        String apiUrl = url;
        StringBuffer param = new StringBuffer();
        int i = 0;
//        for (String key : params.keySet()) {
//            if (i == 0)
//                param.append("?");
//            else
//                param.append("&");
//            param.append(key).append("=").append(params.get(key));
//            i++;
//        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (i == 0)
                param.append("?");
            else
                param.append("&");

            param.append(entry.getKey()).append("=").append(entry.getValue());
            i++;
        }

        apiUrl += param;
        String result = null;
        HttpClient httpclient = HttpClients.custom().build();
        try {
            HttpGet httpGet = new HttpGet(apiUrl);

            // head设置
            if (headMap != null && !headMap.isEmpty()) {
                for (Map.Entry<String, String> entry : headMap.entrySet()) {
                    httpGet.addHeader(entry.getKey(), entry.getValue());
                }
            }

            long begin = System.currentTimeMillis();
            HttpResponse response = httpclient.execute(httpGet);
            logger.info("Http response String is : " + response.toString());
            logger.info("waste:[" + String.valueOf(System.currentTimeMillis() - begin) + "]ms");


            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                HttpEntity entity = response.getEntity();
                result = EntityUtils.toString(entity, "UTF-8");

            } else {

                PayBaseService.monitor(apiUrl, "HttpStatus: " + response.getStatusLine().getStatusCode(), params);
                logger.info("response.getStatusLine().getStatusCode() is : " + response.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            PayBaseService.monitor(apiUrl, e.getMessage(), params);
            logger.error("http error", e);
        }
        return result;
    }

    /**
     * 发送 POST 请求（HTTP），不带输入数据
     *
     * @param apiUrl 请求地址
     * @return String
     */
    public static String doPost(String apiUrl) {

        return doPost(apiUrl, new HashMap<String, Object>());
    }

    /**
     * 发送 POST 请求（HTTP）不带head
     *
     * @param apiUrl API接口URL
     * @param params 参数map
     * @return String
     */
    public static String doPost(String apiUrl, Map<String, Object> params) {

        return doPost(apiUrl, params, null);
    }

    public static String doPostStr(String apiUrl, Map<String, String> params) {

        return doPostStr(apiUrl, params, null);
    }


    /**
     * 发送 POST 请求（HTTP），K-V形式
     *
     * @param apiUrl API接口URL
     * @param params 参数map
     * @return String
     */
    public static String doPost(String apiUrl, Map<String, Object> params, Map<String, String> headMap) {
        //连接池的httpclient
//        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).build();
        //默认的httpclient
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        logger.info("apiUrl is " + apiUrl);

        try {

            httpPost.setConfig(requestConfig);

            // head设置
            if (headMap != null && !headMap.isEmpty()) {
                for (Map.Entry<String, String> entry : headMap.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }

            List<NameValuePair> pairList = new ArrayList<>(params.size());

            for (Map.Entry<String, Object> entry : params.entrySet()) {

                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                pairList.add(pair);
            }

            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));

            Long begin = System.currentTimeMillis();
            response = httpClient.execute(httpPost);
            logger.info("response String is : " + response.toString());
            logger.info("waste:" + String.valueOf(System.currentTimeMillis() - begin));

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                HttpEntity entity = response.getEntity();
                httpStr = EntityUtils.toString(entity, "UTF-8");

            } else {
                PayBaseService.monitor(apiUrl, "HttpStatus:" + response.getStatusLine().getStatusCode(), params);
                logger.info("response.getStatusLine().getStatusCode() is : " + response.getStatusLine().getStatusCode());
            }

        } catch (IOException e) {
            PayBaseService.monitor(apiUrl, e.getMessage(), params);
            logger.error("http error", e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return httpStr;
    }

    /**
     * 发送 POST 请求（HTTP），K-V形式
     *
     * @param apiUrl API接口URL
     * @param params 参数map
     * @return String
     */
    public static String doPostStr(String apiUrl, Map<String, String> params, Map<String, String> headMap) {
        //连接池的httpclient
//        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).build();
        //默认的httpclient
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        logger.info("apiUrl is " + apiUrl);

        try {

            httpPost.setConfig(requestConfig);

            // head设置
            if (headMap != null && !headMap.isEmpty()) {
                for (Map.Entry<String, String> entry : headMap.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }

            List<NameValuePair> pairList = new ArrayList<>(params.size());

            for (Map.Entry<String, String> entry : params.entrySet()) {

                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                pairList.add(pair);
            }

            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));

            Long begin = System.currentTimeMillis();
            response = httpClient.execute(httpPost);
            logger.info("response String is : " + response.toString());
            logger.info("waste:" + String.valueOf(System.currentTimeMillis() - begin));

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                HttpEntity entity = response.getEntity();
                httpStr = EntityUtils.toString(entity, "UTF-8");

            } else {
                PayBaseService.monitor(apiUrl, "HttpStatus:" + response.getStatusLine().getStatusCode(), params);
                logger.info("response.getStatusLine().getStatusCode() is : " + response.getStatusLine().getStatusCode());
            }

        } catch (IOException e) {
            PayBaseService.monitor(apiUrl, e.getMessage(), params);
            logger.error("http error", e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return httpStr;
    }

    /**
     * 发送 POST 请求（HTTP），JSON形式
     *
     * @param apiUrl   请求地址
     * @param bodyJson 请求体
     * @return String
     */
    public static String doPostBody(String apiUrl, String bodyJson) {

        return doPostBody(apiUrl, bodyJson, new HashMap<String, String>());
    }


    /**
     * 发送 POST 请求（HTTP），JSON形式
     *
     * @param apiUrl   请求URL
     * @param bodyJson 请求体
     * @param headMap  请求头
     * @return String
     */
    public static String doPostBody(String apiUrl, String bodyJson, Map<String, String> headMap) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);

            // head设置

            if (headMap != null && !headMap.isEmpty()) {
                for (Map.Entry<String, String> entry : headMap.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }

            // 解决中文乱码问题
            StringEntity stringEntity = new StringEntity(bodyJson, "UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);

            Long beginTime = System.currentTimeMillis();
            response = httpClient.execute(httpPost);
            logger.info("response String is : " + response.toString());
            logger.info("waste:[" + String.valueOf(System.currentTimeMillis() - beginTime) + "]ms");

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                HttpEntity entity = response.getEntity();
                httpStr = EntityUtils.toString(entity, "UTF-8");
                logger.info("httpStr is " + httpStr);
            } else {
                PayBaseService.monitor(apiUrl, "HttpStatus: " + response.getStatusLine().getStatusCode(), bodyJson);
                logger.info("response.getStatusLine().getStatusCode() is : " + response.getStatusLine().getStatusCode());
            }

        } catch (IOException e) {
            PayBaseService.monitor(apiUrl, e.getMessage(), bodyJson);
            logger.error("http error", e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return httpStr;
    }


    /**
     * 发送 POST 请求（HTTP），K-V形式
     *
     * @param apiUrl API接口URL
     * @param params 参数map
     * @return Map
     * 钱方需要"X-QF-SIGN"验证签名
     */
    public static Map<String, String> doPostForQF(String apiUrl, Map<String, Object> params, Map<String, String> headMap) {
        //连接池的httpclient
//        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).build();
        //默认的httpclient
        Map<String, String> mapRsp = new HashMap<>();
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String httpStr;

        CloseableHttpResponse response = null;

        logger.info("apiUrl is " + apiUrl);

        try {

            HttpPost httpPost = new HttpPost(apiUrl);
            httpPost.setConfig(requestConfig);

            // head设置
            if (headMap != null && !headMap.isEmpty()) {
                for (Map.Entry<String, String> entry : headMap.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }

            List<NameValuePair> pairList = new ArrayList<>(params.size());

            for (Map.Entry<String, Object> entry : params.entrySet()) {

                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                pairList.add(pair);
            }

            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));

            Long beginTime = System.currentTimeMillis();
            response = httpClient.execute(httpPost);
            logger.info("response String is : " + response.toString());
            logger.info("waste:[" + String.valueOf(System.currentTimeMillis() - beginTime) + "]ms");

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                String sign = response.getFirstHeader("X-QF-SIGN").toString();
                mapRsp.put("sign", sign);

                HttpEntity entity = response.getEntity();
                httpStr = EntityUtils.toString(entity, "UTF-8");
                logger.info("response-bady String is: " + httpStr);
                mapRsp.put("httpStr", httpStr);
            } else {
                PayBaseService.monitor(apiUrl, "HttpStatus: " + response.getStatusLine().getStatusCode(), params);
                logger.info("response.getStatusLine().getStatusCode() is : " + response.getStatusLine().getStatusCode());
            }

        } catch (IOException e) {
            PayBaseService.monitor(apiUrl, e.getMessage(), params);
            logger.error("http error", e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mapRsp;
    }

    public static String doPostXml(String apiUrl, String xml) {

        //默认的httpclient
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        logger.info("apiUrl is " + apiUrl);

        try {

            httpPost.setConfig(requestConfig);
            // 解决中文乱码问题
            StringEntity stringEntity = new StringEntity(xml, "UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("text/xml");
            httpPost.setEntity(stringEntity);

            Long beginTime = System.currentTimeMillis();
            response = httpClient.execute(httpPost);
            logger.info("response String is : " + response.toString());
            logger.info("waste:[" + String.valueOf(System.currentTimeMillis() - beginTime) + "]ms");


            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                HttpEntity entity = response.getEntity();
                httpStr = EntityUtils.toString(entity, "UTF-8");
                logger.info("response-bady String is: " + httpStr);

            } else {

                PayBaseService.monitor(apiUrl, "HttpStatus: " + response.getStatusLine().getStatusCode(), xml);
                logger.info("response.getStatusLine().getStatusCode() is : " + response.getStatusLine().getStatusCode());
            }

        } catch (IOException e) {
            PayBaseService.monitor(apiUrl, e.getMessage(), xml);
            logger.error("http error", e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return httpStr;
    }

    /**
     * @param apiUrl
     * @param xml
     * @return
     */
    public static String doPostXmlBill(String apiUrl, String xml, String path, String fileName) {

        CloseableHttpClient httpClient = HttpClients.createDefault();

        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        logger.info("apiUrl is " + apiUrl);

        BufferedWriter writer = null;
        BufferedReader br = null;

        try {

            httpPost.setConfig(requestConfig);

            // 解决中文乱码问题
            StringEntity stringEntity = new StringEntity(xml, "UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("text/xml");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);

            logger.info(" wx download bill response is : " + response.toString());

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                InputStream inputStream = response.getEntity().getContent();

                br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));

                File file = new File(path + File.separator + fileName);

                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

                String result;

                while ((result = br.readLine()) != null) {

                    writer.write(result);
                    writer.newLine();//换行
                }

                writer.flush();

            } else {

                logger.info("response.getStatusLine().getStatusCode() is : " + response.getStatusLine().getStatusCode());
            }

        } catch (IOException e) {
            logger.error("http error ", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return httpStr;
    }


    /**
     * @param apiUrl
     * @param path
     * @param billZipName
     * @return
     */
    public static String doPostAliBill(String apiUrl, String path, String billZipName) {

        CloseableHttpClient httpClient = HttpClients.createDefault();

        String httpStr = null;
        HttpGet httpPost = new HttpGet(apiUrl);
        CloseableHttpResponse response = null;

        logger.info("apiUrl is " + apiUrl);

        FileOutputStream out = null;

        try {

            httpPost.setConfig(requestConfig);

            response = httpClient.execute(httpPost);

            logger.info("response String is : " + response.toString());

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                InputStream in = response.getEntity().getContent();

                File file = new File(path + File.separator + billZipName);

                out = new FileOutputStream(file);

                byte[] b = new byte[1024];
                int len = 0;
                while ((len = in.read(b)) != -1) {
                    out.write(b, 0, len);
                }

            } else {

                logger.info("response.getStatusLine().getStatusCode() is : " + response.getStatusLine().getStatusCode());
            }

        } catch (IOException e) {
            logger.error("http error", e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return httpStr;
    }


    /**
     * @param apiUrl
     * @param xml
     * @return
     */
    public static String doPostXmlXYWX(String apiUrl, String xml, String path, String fileName) {

        CloseableHttpClient httpClient = HttpClients.createDefault();

        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        logger.info("XY apiUrl is " + apiUrl);

        BufferedWriter writer = null;
        BufferedReader br = null;

        try {

            httpPost.setConfig(requestConfig);
            // 解决中文乱码问题
            StringEntity stringEntity = new StringEntity(xml, "UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("text/xml");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);

//            logger.info(" wx download bill response is : " + response.toString());

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                InputStream inputStream = response.getEntity().getContent();

                br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));

                File file = new File(path + File.separator + fileName);

                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

                String result;

                while ((result = br.readLine()) != null) {

                    writer.write(result);
                    writer.newLine();//换行

                }

                writer.flush();

            } else {

                logger.info("response.getStatusLine().getStatusCode() is : " + response.getStatusLine().getStatusCode());
            }

        } catch (IOException e) {
            logger.error("http error ", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return httpStr;
    }


}
