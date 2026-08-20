package com.example.scheduler.mq;

import com.example.scheduler.entity.JobInfo;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class SchedulerMQProducer {

    private static final Logger log = LoggerFactory.getLogger(SchedulerMQProducer.class);
    private static final String TOPIC_JOB_DISPATCH = "scheduler-job-dispatch";

    private final RocketMQTemplate rocketMQTemplate;

    public SchedulerMQProducer(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    public void dispatchJob(JobInfo jobInfo, String triggerTime) {
        JobDispatchMessage message = new JobDispatchMessage(
                jobInfo.getId(),
                jobInfo.getExecutorHandler(),
                jobInfo.getExecutorParam(),
                triggerTime,
                UUID.randomUUID().toString().replace("-", "")
        );
        rocketMQTemplate.send(TOPIC_JOB_DISPATCH, MessageBuilder.withPayload(message).build());
        log.info("任务已分发到 MQ: jobId={}, handler={}, traceId={}",
                jobInfo.getId(), jobInfo.getExecutorHandler(), message.getTraceId());
    }

    public static class JobDispatchMessage {
        private Long jobId;
        private String jobHandler;
        private String executorParam;
        private String triggerTime;
        private String traceId;

        public JobDispatchMessage() {}

        public JobDispatchMessage(Long jobId, String jobHandler, String executorParam, String triggerTime, String traceId) {
            this.jobId = jobId;
            this.jobHandler = jobHandler;
            this.executorParam = executorParam;
            this.triggerTime = triggerTime;
            this.traceId = traceId;
        }

        public Long getJobId() { return jobId; }
        public void setJobId(Long jobId) { this.jobId = jobId; }
        public String getJobHandler() { return jobHandler; }
        public void setJobHandler(String jobHandler) { this.jobHandler = jobHandler; }
        public String getExecutorParam() { return executorParam; }
        public void setExecutorParam(String executorParam) { this.executorParam = executorParam; }
        public String getTriggerTime() { return triggerTime; }
        public void setTriggerTime(String triggerTime) { this.triggerTime = triggerTime; }
        public String getTraceId() { return traceId; }
        public void setTraceId(String traceId) { this.traceId = traceId; }
    }
}