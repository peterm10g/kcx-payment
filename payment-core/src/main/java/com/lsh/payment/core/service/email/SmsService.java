package com.lsh.payment.core.service.email;

import com.lsh.payment.core.service.rest.RestHttpClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Project Name: lsh-payment
 * Created by peter on 17/5/2.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service
public class SmsService {

    private static Logger logger = LoggerFactory.getLogger(SmsService.class);

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private RestHttpClientService restHttpClientService;


    /**
     * 邮件发送同步
     *
     * @param url 内容
     * @return boolean
     */
    public boolean send(String url) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String ssJson = "{\"trade_id\":\"360967650000010035\", \"trade_module\":\"order\", \"pay_way\":\"android\", \"request_amount\":\"0.22\", \"notify_url\":\"http://nnns\", \"return_url\":\"http://return\", \"channel_type\":\"1\"}";

        HttpEntity<String> requestEntity  = new HttpEntity<>(ssJson, headers);

        logger.info(restHttpClientService.postHttpForObject("http://testpay.wmdev2.lsh123.com/pay/payment/prepayment", requestEntity, String.class));

        return true;
    }



    /**
     * 邮件发送异步
     *
     * @param content 内容
     * @return boolean
     */
    public boolean sendAyscEmail(String content) {

        final RestTemplate restTemplate = restHttpClientService.getRestForHttpTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String ssJson = "{\"trade_id\":\"360967650000010037\", \"trade_module\":\"order\", \"pay_way\":\"android\", \"request_amount\":\"0.22\", \"notify_url\":\"http://nnns\", \"return_url\":\"http://return\", \"channel_type\":\"1\"}";

        final HttpEntity<String> requestEntity  = new HttpEntity<>(ssJson, headers);

        threadPoolTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String ds = restTemplate.postForObject("http://testpay.wmdev2.lsh123.com/pay/payment/prepayment", requestEntity, String.class);

                logger.info(ds);
            }
        });

        return true;
    }
}
