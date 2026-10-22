package com.example.employeeboard.importing;

import java.util.List;

/**
 * 导入批次结果视图对象（W08/W09）。
 */
public class ImportTaskDTO {

    private Long taskId;
    private String fileName;
    private Integer totalCount;
    private Integer successCount;
    private Integer failCount;
    private String status;
    /** 明细列表（W09 返回） */
    private List<ImportDetailDO> details;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Integer getFailCount() {
        return failCount;
    }

    public void setFailCount(Integer failCount) {
        this.failCount = failCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ImportDetailDO> getDetails() {
        return details;
    }

    public void setDetails(List<ImportDetailDO> details) {
        this.details = details;
    }
}
