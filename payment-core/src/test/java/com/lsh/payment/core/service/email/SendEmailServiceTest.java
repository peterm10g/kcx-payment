package com.lsh.payment.core.service.email;

import com.lsh.payment.core.common.BaseSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/10/17.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
public class SendEmailServiceTest extends BaseSpringTest {

    @Autowired
    private SendEmailService sendEmailService;

    @Test
    public void sendTest() throws Exception {
        sendEmailService.send("异步邮件发送","java版异步邮件发送",new String[]{"aop@lsh123.com"});
    }
}
