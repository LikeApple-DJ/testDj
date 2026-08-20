package com.example.scheduler.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("job_log")
public class JobLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long jobId;
    private LocalDateTime triggerTime;
    private LocalDateTime finishTime;
    private String executorAddress;
    private Integer status;
    private String result;
    private Integer retryTimes;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }
    public LocalDateTime getTriggerTime() { return triggerTime; }
    public void setTriggerTime(LocalDateTime triggerTime) { this.triggerTime = triggerTime; }
    public LocalDateTime getFinishTime() { return finishTime; }
    public void setFinishTime(LocalDateTime finishTime) { this.finishTime = finishTime; }
    public String getExecutorAddress() { return executorAddress; }
    public void setExecutorAddress(String executorAddress) { this.executorAddress = executorAddress; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public Integer getRetryTimes() { return retryTimes; }
    public void setRetryTimes(Integer retryTimes) { this.retryTimes = retryTimes; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}