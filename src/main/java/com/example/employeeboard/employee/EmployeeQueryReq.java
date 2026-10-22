package com.example.employeeboard.employee;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 员工分页查询请求（W04）。
 */
public class EmployeeQueryReq {

    @NotNull(message = "页码必填")
    @Min(value = 1, message = "页码从1开始")
    private Integer pageNo;

    @NotNull(message = "每页条数必填")
    @Min(1)
    @Max(value = 100, message = "每页最多100条")
    private Integer pageSize = 20;

    /** 工号/姓名模糊搜索 */
    private String keyword;

    private String deptName;

    /** ACTIVE/INACTIVE */
    private String status;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
