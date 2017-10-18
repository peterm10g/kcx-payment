package com.lsh.payment.core.service.email;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

/**
 * lsh-payment
 * Created by peter on 16/7/25.
 */
@Component
public class SendEmailService {

    private static Logger logger = LoggerFactory.getLogger(SendEmailService.class);

    private static final String EMAIL_SENDER = "miaozhuang@lsh123.com";

    @Autowired
    private MailSender mailSender;

    @Value("[${profile.name}]")
    private String environment;


    /**
     * 邮件发送同步
     *
     * @param content 内容
     * @param title   标题
     * @param array   接受用户组
     * @return boolean
     */
    public boolean send(String content, String title, String[] array) {

        if (array == null) {
            return false;
        }
        if (StringUtils.isBlank(content)) {
            return false;
        }
        SimpleMailMessage mail = new SimpleMailMessage();

        try {
            mail.setTo(array);//接受者
            mail.setFrom(EMAIL_SENDER);//发送者
            mail.setSubject(environment + title);//主题
            mail.setText(content);//邮件内容
            mailSender.send(mail);
        } catch (MailException me) {
            logger.error("邮件发送失败", me);
            return false;
        } catch (Exception e) {
            logger.error("邮件发送异常", e);
            return false;
        }
        logger.info("邮件发送成功");
        return true;
    }


}
