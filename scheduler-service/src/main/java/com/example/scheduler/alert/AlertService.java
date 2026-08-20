package com.example.scheduler.alert;

import com.example.scheduler.entity.JobInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    private final DingTalkAlertSender dingTalkSender;
    private final MailAlertSender mailSender;

    public AlertService(DingTalkAlertSender dingTalkSender, MailAlertSender mailSender) {
        this.dingTalkSender = dingTalkSender;
        this.mailSender = mailSender;
    }

    public void notifyJobFailed(JobInfo jobInfo, String errorMessage, int retryCount) {
        String subject = String.format("任务执行失败告警 - %s", jobInfo.getJobName());
        String content = String.format(
                "任务名称: %s\n任务ID: %d\nCron表达式: %s\n错误信息: %s\n重试次数: %d\n",
                jobInfo.getJobName(), jobInfo.getId(),
                jobInfo.getCronExpression(), errorMessage, retryCount
        );
        dingTalkSender.send(subject, content);
        if (jobInfo.getAlertEmail() != null && !jobInfo.getAlertEmail().isEmpty()) {
            mailSender.send(jobInfo.getAlertEmail(), subject, content);
        }
        log.info("告警已发送: jobId={}, 渠道=钉钉/邮件", jobInfo.getId());
    }

    public void notifyJobTimeout(JobInfo jobInfo, long timeoutSeconds) {
        String subject = String.format("任务执行超时告警 - %s", jobInfo.getJobName());
        String content = String.format(
                "任务名称: %s\n任务ID: %d\n超时时长: %d秒\n",
                jobInfo.getJobName(), jobInfo.getId(), timeoutSeconds
        );
        dingTalkSender.send(subject, content);
    }
}