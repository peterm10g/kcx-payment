package com.lsh.payment.core.service.email;

import com.lsh.payment.core.model.Async.EmailModel;


public interface MailSenderTemplate {

    /**
     * 发送邮件
     *
     * @param vo
     */
    boolean sendMail(EmailModel vo);

}
