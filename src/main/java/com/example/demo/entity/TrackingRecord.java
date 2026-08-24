package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tracking_record")
public class TrackingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_name", nullable = false)
    private String apiName;

    @Column(name = "caller_name")
    private String callerName;

    @Column(name = "caller_type")
    private String callerType;

    @Column(name = "caller_level")
    private String callerLevel;

    @Column(name = "caller_dept")
    private String callerDept;

    @Column(name = "extra_info", length = 1000)
    private String extraInfo;

    @Column(name = "call_time", nullable = false)
    private LocalDateTime callTime;

    public TrackingRecord() {
    }

    public TrackingRecord(String apiName, String callerName, String callerType,
                          String callerLevel, String callerDept, String extraInfo,
                          LocalDateTime callTime) {
        this.apiName = apiName;
        this.callerName = callerName;
        this.callerType = callerType;
        this.callerLevel = callerLevel;
        this.callerDept = callerDept;
        this.extraInfo = extraInfo;
        this.callTime = callTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getCallerType() {
        return callerType;
    }

    public void setCallerType(String callerType) {
        this.callerType = callerType;
    }

    public String getCallerLevel() {
        return callerLevel;
    }

    public void setCallerLevel(String callerLevel) {
        this.callerLevel = callerLevel;
    }

    public String getCallerDept() {
        return callerDept;
    }

    public void setCallerDept(String callerDept) {
        this.callerDept = callerDept;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public LocalDateTime getCallTime() {
        return callTime;
    }

    public void setCallTime(LocalDateTime callTime) {
        this.callTime = callTime;
    }
}