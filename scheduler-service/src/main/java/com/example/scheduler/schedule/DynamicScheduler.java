package com.example.scheduler.schedule;

import com.example.scheduler.entity.JobInfo;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DynamicScheduler {

    private static final Logger log = LoggerFactory.getLogger(DynamicScheduler.class);

    private final Scheduler scheduler;

    public DynamicScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void registerJob(JobInfo jobInfo) {
        try {
            JobKey jobKey = JobKey.jobKey(String.valueOf(jobInfo.getId()), jobInfo.getJobGroup());

            if (scheduler.checkExists(jobKey)) {
                log.info("任务已存在，更新调度: jobId={}", jobInfo.getId());
                scheduler.deleteJob(jobKey);
            }

            JobDetail jobDetail = JobBuilder.newJob(DispatchJob.class)
                    .withIdentity(jobKey)
                    .usingJobData("jobId", jobInfo.getId())
                    .usingJobData("executorHandler", jobInfo.getExecutorHandler())
                    .usingJobData("executorParam", jobInfo.getExecutorParam())
                    .build();

            CronScheduleBuilder cronSchedule = CronScheduleBuilder.cronSchedule(jobInfo.getCronExpression());
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobInfo.getId() + "-trigger", jobInfo.getJobGroup())
                    .withSchedule(cronSchedule)
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            log.info("任务已注册到调度器: jobId={}, cron={}", jobInfo.getId(), jobInfo.getCronExpression());
        } catch (SchedulerException e) {
            log.error("注册任务失败: jobId={}", jobInfo.getId(), e);
        }
    }

    public void unregisterJob(Long jobId) {
        try {
            JobKey jobKey = JobKey.jobKey(String.valueOf(jobId));
            scheduler.deleteJob(jobKey);
            log.info("任务已从调度器移除: jobId={}", jobId);
        } catch (SchedulerException e) {
            log.error("移除任务失败: jobId={}", jobId, e);
        }
    }
}