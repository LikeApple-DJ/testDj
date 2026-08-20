package com.example.scheduler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.scheduler.entity.JobLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobLogMapper extends BaseMapper<JobLog> {
}