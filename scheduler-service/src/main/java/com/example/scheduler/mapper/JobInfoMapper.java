package com.example.scheduler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.scheduler.entity.JobInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobInfoMapper extends BaseMapper<JobInfo> {
}