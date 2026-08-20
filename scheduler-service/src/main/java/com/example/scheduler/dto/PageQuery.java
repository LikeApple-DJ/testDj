package com.example.scheduler.dto;

public class PageQuery {
    private int page = 1;
    private int size = 10;
    private String keyword;
    private String jobGroup;
    private Integer status;

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getJobGroup() { return jobGroup; }
    public void setJobGroup(String jobGroup) { this.jobGroup = jobGroup; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}