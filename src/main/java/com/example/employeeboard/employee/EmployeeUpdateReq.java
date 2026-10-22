package com.example.employeeboard.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 编辑员工请求（W02），仅更新传入字段，empNo 仅用于定位。
 */
public class EmployeeUpdateReq {

    @NotBlank(message = "工号必填")
    @Size(max = 32)
    private String empNo;

    @Size(max = 64)
    private String empName;

    @Size(max = 64)
    private String deptName;

    @Size(max = 64)
    private String position;

    @Size(max = 128)
    private String email;

    @Size(max = 32)
    private String phone;

    private String hireDate;

    /** ACTIVE/INACTIVE */
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
