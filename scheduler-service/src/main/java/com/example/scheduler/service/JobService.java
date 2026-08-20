package com.example.scheduler.service;

import com.example.scheduler.dto.JobInfoDTO;
import com.example.scheduler.dto.PageQuery;
import com.example.scheduler.dto.PageResult;
import com.example.scheduler.entity.JobLog;

public interface JobService {
    Long addJob(JobInfoDTO jobInfoDTO);
    boolean updateJob(JobInfoDTO jobInfoDTO);
    boolean deleteJob(Long jobId);
    boolean triggerJob(Long jobId);
    boolean pauseJob(Long jobId);
    boolean resumeJob(Long jobId);
    PageResult<JobInfoDTO> listJobs(PageQuery query);
    PageResult<JobLog> listJobLogs(PageQuery query);
}