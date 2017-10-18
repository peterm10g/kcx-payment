package com.lsh.payment.core.util;

import com.lsh.payment.core.exception.BusinessException;
import com.lsh.payment.core.strategy.config.WxPayConfig;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Arrays;

/**
 * HTTP 请求工具类
 *
 * @author : miaozhuang
 * @version : 1.0.0
 * @date : 2016/8/24
 * @see : TODO
 */
public class HttpsClientUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpsClientUtil.class);

    private static SSLConnectionSocketFactory socketFactory;
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT = 30000;
    private static SSLContext sslContext;

    static {

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
        // 在提交请求之前 测试连接是否可用
        requestConfig = configBuilder.build();
    }


    /**
     * @param apiUrl
     * @param xml
     * @return
     */
    public static String doPostXml(String apiUrl, String xml) {

//        if (null == sslContext) {
//            sslContext = initSSLContext();
//        }
//
//        if (socketFactory == null) {
//            socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
//        }






        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        logger.info("apiUrl is " + apiUrl);
        CloseableHttpClient httpClient = null;
        try {
            httpClient = create();
            httpPost.setConfig(requestConfig);

            // 解决中文乱码问题
            StringEntity stringEntity = new StringEntity(xml, "UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("text/xml");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);

            logger.info(" wx download bill response is : " + response.toString());

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                HttpEntity entity = response.getEntity();
                httpStr = EntityUtils.toString(entity, "UTF-8");

            } else {

                logger.info("response.getStatusLine().getStatusCode() is : " + response.getStatusLine().getStatusCode());
            }

        } catch (IOException e) {
            logger.error("http error ", e);
        } catch (Exception e) {
            logger.error("http error ", e);
        } finally {

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


    private static CloseableHttpClient create() throws Exception {
        if (null == sslContext) {
            sslContext = initSSLContext();
        }
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslContext,
                new String[] { "TLSv1" },
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());

//        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
//            sslcontext,
//            new String[] { "TLSv1" },
//            null,
//            SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build();

        return httpclient;
    }



    private static SSLContext initSSLContext() {
        if (StringUtils.isBlank(WxPayConfig.GROUPON_MCHID)) {
            throw new IllegalArgumentException("请确保商户号mchId已设置");
        }

        if (StringUtils.isBlank(WxPayConfig.KEY_PATH)) {
            throw new IllegalArgumentException("请确保证书文件地址keyPath已配置");
        }

        InputStream inputStream;
        final String prefix = "classpath:";
        if (WxPayConfig.KEY_PATH.startsWith(prefix)) {
            inputStream = WxPayConfig.class.getResourceAsStream(WxPayConfig.KEY_PATH.replace(prefix, ""));
        } else {
            try {
                File file = new File(WxPayConfig.KEY_PATH);
                if (!file.exists()) {
                    throw new BusinessException("证书文件【" + file.getPath() + "】不存在！");
                }

                inputStream = new FileInputStream(file);
            } catch (IOException e) {
                throw new BusinessException("证书文件有问题，请核实！", e);
            }
        }

        try {
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            char[] partnerId2charArray = WxPayConfig.GROUPON_MCHID.toCharArray();
            keystore.load(inputStream, partnerId2charArray);
            sslContext = SSLContexts.custom().loadKeyMaterial(keystore, partnerId2charArray).build();
            return sslContext;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("证书文件有问题，请核实！", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }


//    SSLContext sslcontext = SSLContexts.custom()
//            .loadTrustMaterial(new File("my.keystore"), "nopassword".toCharArray(),
//                    new TrustSelfSignedStrategy())
//            .build();
//    // Allow TLSv1 protocol only
//    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
//            sslcontext,
//            new String[] { "TLSv1" },
//            null,
//            SSLConnectionSocketFactory.getDefaultHostnameVerifier());
//    CloseableHttpClient httpclient = HttpClients.custom()
//            .setSSLSocketFactory(sslsf)
//            .build();


    public static CloseableHttpClient createHttpsClient() {
        if (socketFactory == null) {
            socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
        }
        // 配置config
        RequestConfig config = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).setExpectContinueEnabled(true)
                .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST)).setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();

        // 设置连接池
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory)
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);

        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(10);

        return HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(config).build();
    }





}
