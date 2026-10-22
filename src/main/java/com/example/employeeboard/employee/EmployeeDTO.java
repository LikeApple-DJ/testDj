package com.example.employeeboard.employee;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 员工视图对象。
 */
public class EmployeeDTO {

    private Long id;
    private String empNo;
    private String empName;
    private String deptName;
    private String position;
    private String email;
    /** 非白名单用户场景下中间四位打码 */
    private String phone;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date hireDate;
    private String status;
    private String source;
    /** 最近一期预算金额（详情页摘要） */
    private BigDecimal latestBudgetAmount;
    /** 最近一期预算周期（详情页摘要） */
    private String latestBudgetPeriod;

    public static EmployeeDTO fromDO(EmployeeDO record) {
        if (record == null) {
            return null;
        }
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(record.getId());
        dto.setEmpNo(record.getEmpNo());
        dto.setEmpName(record.getEmpName());
        dto.setDeptName(record.getDeptName());
        dto.setPosition(record.getPosition());
        dto.setEmail(record.getEmail());
        dto.setPhone(record.getPhone());
        dto.setHireDate(record.getHireDate());
        dto.setStatus(record.getStatus());
        dto.setSource(record.getSource());
        return dto;
    }

    /**
     * 手机号中间四位打码。
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public BigDecimal getLatestBudgetAmount() {
        return latestBudgetAmount;
    }

    public void setLatestBudgetAmount(BigDecimal latestBudgetAmount) {
        this.latestBudgetAmount = latestBudgetAmount;
    }

    public String getLatestBudgetPeriod() {
        return latestBudgetPeriod;
    }

    public void setLatestBudgetPeriod(String latestBudgetPeriod) {
        this.latestBudgetPeriod = latestBudgetPeriod;
    }
}
