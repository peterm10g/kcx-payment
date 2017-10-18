package com.lsh.payment.core.strategy.emailStrategy.impl;


import com.lsh.payment.core.model.Async.EmailModel;
import com.lsh.payment.core.strategy.emailStrategy.MailStrategy;


public class HTMLStrategy implements MailStrategy {

    public String message(EmailModel vo) {
        return vo.getContent();
    }
}
