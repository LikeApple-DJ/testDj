package com.example.scheduler.mq;

import com.example.scheduler.entity.JobLog;
import com.example.scheduler.mapper.JobLogMapper;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RocketMQMessageListener(topic = "scheduler-job-callback", consumerGroup = "scheduler-callback-consumer")
public class SchedulerMQConsumer implements RocketMQListener<SchedulerMQProducer.JobCallbackMessage> {

    private static final Logger log = LoggerFactory.getLogger(SchedulerMQConsumer.class);
    private final JobLogMapper jobLogMapper;

    public SchedulerMQConsumer(JobLogMapper jobLogMapper) {
        this.jobLogMapper = jobLogMapper;
    }

    @Override
    public void onMessage(SchedulerMQProducer.JobCallbackMessage message) {
        log.info("收到执行回调: jobId={}, status={}", message.getJobId(), message.getStatus());

        JobLog jobLog = new JobLog();
        jobLog.setJobId(message.getJobId());
        jobLog.setStatus(message.getStatus());
        jobLog.setResult(message.getResult());
        jobLog.setFinishTime(message.getFinishTime());
        jobLogMapper.insert(jobLog);
    }

    public static class JobCallbackMessage {
        private Long jobId;
        private String traceId;
        private Integer status;
        private String result;
        private LocalDateTime finishTime;

        public Long getJobId() { return jobId; }
        public void setJobId(Long jobId) { this.jobId = jobId; }
        public String getTraceId() { return traceId; }
        public void setTraceId(String traceId) { this.traceId = traceId; }
        public Integer getStatus() { return status; }
        public void setStatus(Integer status) { this.status = status; }
        public String getResult() { return result; }
        public void setResult(String result) { this.result = result; }
        public LocalDateTime getFinishTime() { return finishTime; }
        public void setFinishTime(LocalDateTime finishTime) { this.finishTime = finishTime; }
    }
}