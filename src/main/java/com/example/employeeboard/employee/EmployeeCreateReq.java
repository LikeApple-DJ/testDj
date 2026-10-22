package com.example.employeeboard.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 新增员工请求（W01）。
 */
public class EmployeeCreateReq {

    @NotBlank(message = "工号必填")
    @Size(max = 32, message = "工号长度不能超过32")
    private String empNo;

    @NotBlank(message = "姓名必填")
    @Size(max = 64, message = "姓名长度不能超过64")
    private String empName;

    @NotBlank(message = "部门必填")
    @Size(max = 64, message = "部门长度不能超过64")
    private String deptName;

    @Size(max = 64)
    private String position;

    @Size(max = 128)
    private String email;

    @Size(max = 32)
    private String phone;

    /** 入职日期 yyyy-MM-dd */
    private String hireDate;

    public String getEmpNo() {
        return empNo;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHireDate() {
        return hireDate;
    }

    public void setHireDate(String hireDate) {
        this.hireDate = hireDate;
    }
}
