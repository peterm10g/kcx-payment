package com.lsh.payment.core.model.Async;

/**
 * Project Name: lsh-payment
 * Created by jingyuan
 * Date: 16/12/22
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.model.Async
 * desc:
 */
public class EmailModel {

    String content;
    String title;
    String[] toEmails;

    public EmailModel(String content, String title, String[] toEmails) {
        this.content = content;
        this.title = title;
        this.toEmails = toEmails;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getToEmails() {
        return toEmails;
    }

    public void setToEmails(String[] toEmails) {
        this.toEmails = toEmails;
    }
}
