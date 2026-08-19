package com.orgarch.employee;

import java.time.LocalDate;

public class EmployeeVo {
    private Long id;
    private String name;
    private String employeeNo;
    private String phone;
    private Long deptId;
    private String position;
    private String status;
    private LocalDate resignDate;
    private Integer version;

    public EmployeeVo() {}

    public EmployeeVo(Employee e) {
        this.id = e.getId();
        this.name = e.getName();
        this.employeeNo = e.getEmployeeNo();
        this.phone = e.getPhone();
        this.deptId = e.getDeptId();
        this.position = e.getPosition();
        this.status = e.getStatus();
        this.resignDate = e.getResignDate();
        this.version = e.getVersion();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmployeeNo() { return employeeNo; }
    public String getPhone() { return phone; }
    public Long getDeptId() { return deptId; }
    public String getPosition() { return position; }
    public String getStatus() { return status; }
    public LocalDate getResignDate() { return resignDate; }
    public Integer getVersion() { return version; }
}
