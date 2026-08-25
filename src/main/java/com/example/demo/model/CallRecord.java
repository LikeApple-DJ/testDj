package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "call_records")
public class CallRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "caller_id")
    private String callerId;

    @Column(name = "caller_type")
    private String callerType;

    @Column(name = "caller_level")
    private String callerLevel;

    @Column(name = "caller_dept")
    private String callerDept;

    @Column(name = "api_name")
    private String apiName;

    @Column(name = "call_time")
    private LocalDateTime callTime;

    @Column(name = "response_time")
    private Long responseTime;

    public CallRecord() {}

    public CallRecord(String callerId, String callerType, String callerLevel,
                      String callerDept, String apiName, LocalDateTime callTime,
                      Long responseTime) {
        this.callerId = callerId;
        this.callerType = callerType;
        this.callerLevel = callerLevel;
        this.callerDept = callerDept;
        this.apiName = apiName;
        this.callTime = callTime;
        this.responseTime = responseTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCallerId() { return callerId; }
    public void setCallerId(String callerId) { this.callerId = callerId; }

    public String getCallerType() { return callerType; }
    public void setCallerType(String callerType) { this.callerType = callerType; }

    public String getCallerLevel() { return callerLevel; }
    public void setCallerLevel(String callerLevel) { this.callerLevel = callerLevel; }

    public String getCallerDept() { return callerDept; }
    public void setCallerDept(String callerDept) { this.callerDept = callerDept; }

    public String getApiName() { return apiName; }
    public void setApiName(String apiName) { this.apiName = apiName; }

    public LocalDateTime getCallTime() { return callTime; }
    public void setCallTime(LocalDateTime callTime) { this.callTime = callTime; }

    public Long getResponseTime() { return responseTime; }
    public void setResponseTime(Long responseTime) { this.responseTime = responseTime; }
}