package com.lsh.payment.core.service.email.impl;


import com.lsh.payment.core.model.Async.EmailModel;
import com.lsh.payment.core.service.email.MailSenderTemplate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


@Component
public class MailSenderTemplateImpl implements MailSenderTemplate {

    private Logger logger = LoggerFactory.getLogger(MailSenderTemplateImpl.class);

    private static final String EMAIL_SENDER = "miaozhuang@lsh123.com";

    @Autowired
    private JavaMailSender mailSender;
    @Value("[${profile.name}]")
    private String environment;

    public boolean sendMail(EmailModel vo){

        if (vo.getToEmails() == null) {
            return false;
        }
        if (StringUtils.isBlank(vo.getContent())) {
            return false;
        }
        if (StringUtils.isBlank(vo.getTitle())) {
            return false;
        }
        SimpleMailMessage mail = new SimpleMailMessage();

        try {
            mail.setTo(vo.getToEmails());//接受者
            mail.setFrom(EMAIL_SENDER);//发送者
            mail.setSubject(environment + vo.getTitle());//主题
            mail.setText(vo.getContent());//邮件内容
            logger.info("邮件{}发送,内容{}" ,vo.getTitle(),vo.getContent());
            mailSender.send(mail);

            logger.info("邮件{}发送成功", vo.getTitle());
            return true;
        } catch (MailException me) {
            logger.error("邮件发送失败", me);
        } catch (Exception e) {
            logger.error("邮件发送异常", e);
        }

        return false;
    }


}
