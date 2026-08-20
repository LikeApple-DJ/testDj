package com.example.scheduler.retry;

import com.example.scheduler.entity.JobInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RetryService {

    private static final Logger log = LoggerFactory.getLogger(RetryService.class);

    public long computeNextRetryDelay(JobInfo jobInfo, int currentRetryCount) {
        long baseInterval = jobInfo.getRetryInterval() != null && jobInfo.getRetryInterval() > 0
                ? jobInfo.getRetryInterval()
                : 60;
        long delay = baseInterval * (long) Math.pow(2, currentRetryCount);
        return Math.min(delay, 3600);
    }

    public boolean shouldRetry(JobInfo jobInfo, int currentRetryCount) {
        int maxRetries = jobInfo.getMaxRetryTimes() != null ? jobInfo.getMaxRetryTimes() : 3;
        return currentRetryCount < maxRetries;
    }

    public void scheduleRetry(JobInfo jobInfo, int currentRetryCount) {
        if (!shouldRetry(jobInfo, currentRetryCount)) {
            log.warn("任务已达最大重试次数: jobId={}, retryCount={}", jobInfo.getId(), currentRetryCount);
            return;
        }
        long delay = computeNextRetryDelay(jobInfo, currentRetryCount);
        log.info("计划重试: jobId={}, 第{}次重试, 延迟{}秒", jobInfo.getId(), currentRetryCount + 1, delay);
    }
}