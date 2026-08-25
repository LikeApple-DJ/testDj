package com.testdj.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_call_log")
public class ApiCallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_name", nullable = false, length = 100)
    private String apiName;

    @Column(name = "caller", nullable = false, length = 100)
    private String caller;

    @Column(name = "department", nullable = false, length = 100)
    private String department;

    @Column(name = "level", nullable = false, length = 50)
    private String level;

    @Column(name = "type", nullable = false, length = 100)
    private String type;

    @Column(name = "call_time", nullable = false)
    private LocalDateTime callTime;

    public ApiCallLog() {
    }

    public ApiCallLog(String apiName, String caller, String department, String level, String type, LocalDateTime callTime) {
        this.apiName = apiName;
        this.caller = caller;
        this.department = department;
        this.level = level;
        this.type = type;
        this.callTime = callTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getApiName() { return apiName; }
    public void setApiName(String apiName) { this.apiName = apiName; }

    public String getCaller() { return caller; }
    public void setCaller(String caller) { this.caller = caller; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDateTime getCallTime() { return callTime; }
    public void setCallTime(LocalDateTime callTime) { this.callTime = callTime; }
}