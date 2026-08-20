package com.example.scheduler.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("job_info")
public class JobInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String jobName;
    private String jobDesc;
    private String jobGroup;
    private String cronExpression;
    private String executorHandler;
    private String executorParam;
    private Integer maxRetryTimes;
    private Integer retryInterval;
    private String alertEmail;
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }
    public String getJobDesc() { return jobDesc; }
    public void setJobDesc(String jobDesc) { this.jobDesc = jobDesc; }
    public String getJobGroup() { return jobGroup; }
    public void setJobGroup(String jobGroup) { this.jobGroup = jobGroup; }
    public String getCronExpression() { return cronExpression; }
    public void setCronExpression(String cronExpression) { this.cronExpression = cronExpression; }
    public String getExecutorHandler() { return executorHandler; }
    public void setExecutorHandler(String executorHandler) { this.executorHandler = executorHandler; }
    public String getExecutorParam() { return executorParam; }
    public void setExecutorParam(String executorParam) { this.executorParam = executorParam; }
    public Integer getMaxRetryTimes() { return maxRetryTimes; }
    public void setMaxRetryTimes(Integer maxRetryTimes) { this.maxRetryTimes = maxRetryTimes; }
    public Integer getRetryInterval() { return retryInterval; }
    public void setRetryInterval(Integer retryInterval) { this.retryInterval = retryInterval; }
    public String getAlertEmail() { return alertEmail; }
    public void setAlertEmail(String alertEmail) { this.alertEmail = alertEmail; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}