package com.lsh.payment.core.service.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Project Name: lsh-payment
 * Created by peter on 17/5/4.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Component
public class EmailConfig {

    /**
     * 邮件接收人(一般)
     */
    @Value("#{'${to.user.email}'.split(',')}")
    private List<String> mailReceivers;

    /**
     * 邮件接收人(财务)
     */
    @Value("#{'${to.finance.email}'.split(',')}")
    private List<String> financeReceivers;

    /**
     * 邮件发送者
     */
    @Value("${mail.sender}")
    private String mailSender;

    /**
     * 邮件环境
     */
    @Value("[${profile.name}]")
    private String environment;

    public List<String> getMailReceivers() {
        return mailReceivers;
    }

    public void setMailReceivers(List<String> mailReceivers) {
        this.mailReceivers = mailReceivers;
    }

    public List<String> getFinanceReceivers() {
        return financeReceivers;
    }

    public void setFinanceReceivers(List<String> financeReceivers) {
        this.financeReceivers = financeReceivers;
    }

    public String getMailSender() {
        return mailSender;
    }

    public void setMailSender(String mailSender) {
        this.mailSender = mailSender;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }


    public String[] mailReceivers(){
        if(mailReceivers == null || mailReceivers.isEmpty()){
            return null;
        }

        return mailReceivers.toArray(new String[mailReceivers.size()]);
    }

    public String[] financeReceivers(){
        if(financeReceivers == null || financeReceivers.isEmpty()){
            return null;
        }
        return financeReceivers.toArray(new String[financeReceivers.size()]);
    }
}
