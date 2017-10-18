package com.lsh.payment.core.strategy.emailStrategy;

import com.lsh.payment.core.model.Async.EmailModel;


public interface MailStrategy {

    String message(EmailModel vo);
}
