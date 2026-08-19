package com.orgarch.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EmployeeCreateRequest {
    @NotBlank private String name;
    @NotBlank private String employeeNo;
    @NotBlank private String phone;
    @NotNull private Long deptId;
    private String position;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
}
