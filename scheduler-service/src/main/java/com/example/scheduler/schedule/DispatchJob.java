package com.example.scheduler.schedule;

import com.example.scheduler.entity.JobInfo;
import com.example.scheduler.mapper.JobInfoMapper;
import com.example.scheduler.mq.SchedulerMQProducer;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@DisallowConcurrentExecution
public class DispatchJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(DispatchJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        long jobId = context.getJobDetail().getJobDataMap().getLong("jobId");
        log.info("Quartz 触发调度: jobId={}", jobId);

        JobInfoMapper jobInfoMapper = ApplicationContextProvider.getBean(JobInfoMapper.class);
        SchedulerMQProducer mqProducer = ApplicationContextProvider.getBean(SchedulerMQProducer.class);

        JobInfo jobInfo = jobInfoMapper.selectById(jobId);
        if (jobInfo == null || jobInfo.getStatus() != 1) {
            log.warn("任务不存在或已禁用，跳过调度: jobId={}", jobId);
            return;
        }

        String triggerTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        mqProducer.dispatchJob(jobInfo, triggerTime);
    }
}