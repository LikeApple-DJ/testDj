package com.example.scheduler.schedule;

import com.example.scheduler.entity.JobInfo;
import com.example.scheduler.mapper.JobInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class ScheduleService {

    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);

    private final JobInfoMapper jobInfoMapper;
    private final DynamicScheduler dynamicScheduler;

    public ScheduleService(JobInfoMapper jobInfoMapper, DynamicScheduler dynamicScheduler) {
        this.jobInfoMapper = jobInfoMapper;
        this.dynamicScheduler = dynamicScheduler;
    }

    @PostConstruct
    public void init() {
        List<JobInfo> enabledJobs = jobInfoMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<JobInfo>()
                        .eq(JobInfo::getStatus, 1)
        );
        for (JobInfo job : enabledJobs) {
            dynamicScheduler.registerJob(job);
        }
        log.info("调度引擎初始化完成，已注册 {} 个任务", enabledJobs.size());
    }

    public void registerJob(JobInfo jobInfo) {
        dynamicScheduler.registerJob(jobInfo);
    }

    public void unregisterJob(Long jobId) {
        dynamicScheduler.unregisterJob(jobId);
    }
}