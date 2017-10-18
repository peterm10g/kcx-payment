package com.lsh.payment.core.service.rest;

import org.apache.http.Header;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Project Name: lsh-payment
 * Created by peter on 17/4/28.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Component
@Lazy(false)
public class RestHttpClientService implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestSimpleClientService.class);

    private RestTemplate restForHttpTemplate;

    private RestTemplate restForHttpsTemplate;

    public RestTemplate getRestForHttpTemplate() {
        return restForHttpTemplate;
    }

    public RestTemplate getRestForHttpsTemplate() {
        return restForHttpsTemplate;
    }

    private RestHttpClientService() {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

        restForHttpTemplate = new RestTemplate(this.getMessageConverters());
        restForHttpTemplate.setRequestFactory(this.getHttpRequestFactory(false));
        restForHttpTemplate.setErrorHandler(new DefaultResponseErrorHandler());

        restForHttpsTemplate = new RestTemplate(this.getMessageConverters());
        restForHttpsTemplate.setRequestFactory(this.getHttpRequestFactory(true));
        restForHttpTemplate.setErrorHandler(new DefaultResponseErrorHandler());

        LOGGER.info("RestHttpClientService init complete");
    }

    /**
     * 获取http请求客户端
     * @return CloseableHttpClient
     */
    private CloseableHttpClient getHttpClient(){
        // 长连接保持30秒
        PoolingHttpClientConnectionManager pollingConnectionManager = new PoolingHttpClientConnectionManager(30, TimeUnit.SECONDS);
        // 总连接数
        pollingConnectionManager.setMaxTotal(1000);
        // 同路由的并发数
        pollingConnectionManager.setDefaultMaxPerRoute(1000);

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setConnectionManager(pollingConnectionManager);
        // 重试次数，默认是3次，没有开启
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(2, true));
        // 保持长连接配置，需要在头添加Keep-Alive
        httpClientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());

        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.16 Safari/537.36"));
        headers.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
        headers.add(new BasicHeader("Accept-Language", "zh-CN"));
        headers.add(new BasicHeader("Connection", "Keep-Alive"));

        httpClientBuilder.setDefaultHeaders(headers);

        return httpClientBuilder.build();
    }

    /**
     * 获取https请求客户端
     * @return CloseableHttpClient
     */
    private CloseableHttpClient getHttpsClient()  {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        SSLContext sslContext = null;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    return true;
                }
            }).build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        httpClientBuilder.setSSLContext(sslContext);

        HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;

        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();

        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connMgr.setMaxTotal(200);
        connMgr.setDefaultMaxPerRoute(100);
        httpClientBuilder.setConnectionManager(connMgr);

        return httpClientBuilder.build();
    }

    /**
     * 获取RestTemplate工厂
     * @param httpsFlag 是否https请求
     * @return HttpComponentsClientHttpRequestFactory
     */
    private HttpComponentsClientHttpRequestFactory getHttpRequestFactory(boolean httpsFlag){
        CloseableHttpClient httpClient;
        if(httpsFlag){
            httpClient = this.getHttpsClient();
        }else{
            httpClient = this.getHttpClient();
        }

        // httpClient连接配置，底层是配置RequestConfig
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        // 连接超时
        clientHttpRequestFactory.setConnectTimeout(5000);
        // 数据读取超时时间，即SocketTimeout
        clientHttpRequestFactory.setReadTimeout(5000);
        // 连接不够用的等待时间，不宜过长，必须设置，比如连接不够用时，时间过长将是灾难性的
        clientHttpRequestFactory.setConnectionRequestTimeout(200);
        // 缓冲请求数据，默认值是true。通过POST或者PUT大量发送数据时，建议将此属性更改为false，以免耗尽内存。
        clientHttpRequestFactory.setBufferRequestBody(false);

        return clientHttpRequestFactory;
    }

    /**
     * 获取内容解析器
     * @return HttpMessageConverter
     */
    private List<HttpMessageConverter<?>> getMessageConverters(){
        // 添加内容转换器
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        messageConverters.add(new FormHttpMessageConverter());
        messageConverters.add(new MappingJackson2XmlHttpMessageConverter());
        messageConverters.add(new MappingJackson2HttpMessageConverter());

        return messageConverters;
    }

    /**
     * 发送Post请求
     * @param url  请求地址
     * @param requestEntity  请求信息
     * @param responseType   返回值没醒
     * @param <T>            返回值参数
     * @return               返回值
     */
    public <T> T postHttpForObject(String url, HttpEntity<T> requestEntity,Class<T> responseType){

        return restForHttpTemplate.postForObject(url, requestEntity, responseType);
    }

    /**
     * 发送Post请求
     * @param url  请求地址
     * @param requestEntity  请求信息
     * @param responseType   返回值没醒
     * @param <T>            返回值参数
     * @return               返回值
     */
    public <T> T postHttpsForObject(String url, HttpEntity<T> requestEntity,Class<T> responseType){

        return restForHttpsTemplate.postForObject(url, requestEntity, responseType);
    }


}
