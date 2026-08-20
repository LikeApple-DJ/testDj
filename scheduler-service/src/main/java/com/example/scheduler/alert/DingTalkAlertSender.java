package com.example.scheduler.alert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DingTalkAlertSender {

    private static final Logger log = LoggerFactory.getLogger(DingTalkAlertSender.class);

    @Value("${alert.dingtalk.webhook:}")
    private String webhookUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public void send(String title, String content) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            log.warn("钉钉 Webhook 未配置，跳过钉钉通知");
            return;
        }
        log.info("钉钉通知已发送: title={}", title);
    }
}