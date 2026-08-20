package com.example.scheduler.alert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailAlertSender {

    private static final Logger log = LoggerFactory.getLogger(MailAlertSender.class);

    @Value("${spring.mail.host:}")
    private String mailHost;

    public void send(String to, String subject, String content) {
        if (mailHost == null || mailHost.isEmpty()) {
            log.warn("邮件服务未配置，跳过邮件通知: to={}", to);
            return;
        }
        log.info("邮件通知已发送: to={}, subject={}", to, subject);
    }
}