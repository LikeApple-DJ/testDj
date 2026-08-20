package com.example.scheduler.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.scheduler.dto.JobInfoDTO;
import com.example.scheduler.dto.PageQuery;
import com.example.scheduler.dto.PageResult;
import com.example.scheduler.entity.JobInfo;
import com.example.scheduler.entity.JobLog;
import com.example.scheduler.mapper.JobInfoMapper;
import com.example.scheduler.mapper.JobLogMapper;
import com.example.scheduler.service.JobService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    private final JobInfoMapper jobInfoMapper;
    private final JobLogMapper jobLogMapper;

    public JobServiceImpl(JobInfoMapper jobInfoMapper, JobLogMapper jobLogMapper) {
        this.jobInfoMapper = jobInfoMapper;
        this.jobLogMapper = jobLogMapper;
    }

    @Override
    @Transactional
    public Long addJob(JobInfoDTO jobInfoDTO) {
        JobInfo jobInfo = new JobInfo();
        BeanUtils.copyProperties(jobInfoDTO, jobInfo);
        jobInfo.setStatus(1);
        jobInfoMapper.insert(jobInfo);
        return jobInfo.getId();
    }

    @Override
    @Transactional
    public boolean updateJob(JobInfoDTO jobInfoDTO) {
        JobInfo jobInfo = new JobInfo();
        BeanUtils.copyProperties(jobInfoDTO, jobInfo);
        return jobInfoMapper.updateById(jobInfo) > 0;
    }

    @Override
    @Transactional
    public boolean deleteJob(Long jobId) {
        return jobInfoMapper.deleteById(jobId) > 0;
    }

    @Override
    public boolean triggerJob(Long jobId) {
        return true;
    }

    @Override
    @Transactional
    public boolean pauseJob(Long jobId) {
        JobInfo jobInfo = jobInfoMapper.selectById(jobId);
        if (jobInfo == null) return false;
        jobInfo.setStatus(0);
        return jobInfoMapper.updateById(jobInfo) > 0;
    }

    @Override
    @Transactional
    public boolean resumeJob(Long jobId) {
        JobInfo jobInfo = jobInfoMapper.selectById(jobId);
        if (jobInfo == null) return false;
        jobInfo.setStatus(1);
        return jobInfoMapper.updateById(jobInfo) > 0;
    }

    @Override
    public PageResult<JobInfoDTO> listJobs(PageQuery query) {
        LambdaQueryWrapper<JobInfo> wrapper = new LambdaQueryWrapper<>();
        if (query.getKeyword() != null && !query.getKeyword().isEmpty()) {
            wrapper.like(JobInfo::getJobName, query.getKeyword());
        }
        if (query.getJobGroup() != null && !query.getJobGroup().isEmpty()) {
            wrapper.eq(JobInfo::getJobGroup, query.getJobGroup());
        }
        if (query.getStatus() != null) {
            wrapper.eq(JobInfo::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(JobInfo::getCreateTime);

        Page<JobInfo> page = new Page<>(query.getPage(), query.getSize());
        Page<JobInfo> result = jobInfoMapper.selectPage(page, wrapper);

        List<JobInfoDTO> dtoList = result.getRecords().stream().map(job -> {
            JobInfoDTO dto = new JobInfoDTO();
            BeanUtils.copyProperties(job, dto);
            return dto;
        }).collect(Collectors.toList());

        return new PageResult<>(result.getTotal(), query.getPage(), query.getSize(), dtoList);
    }

    @Override
    public PageResult<JobLog> listJobLogs(PageQuery query) {
        LambdaQueryWrapper<JobLog> wrapper = new LambdaQueryWrapper<>();
        if (query.getKeyword() != null) {
            try {
                wrapper.eq(JobLog::getJobId, Long.parseLong(query.getKeyword()));
            } catch (NumberFormatException ignored) {}
        }
        wrapper.orderByDesc(JobLog::getCreateTime);

        Page<JobLog> page = new Page<>(query.getPage(), query.getSize());
        Page<JobLog> result = jobLogMapper.selectPage(page, wrapper);

        return new PageResult<>(result.getTotal(), query.getPage(), query.getSize(), result.getRecords());
    }
}