package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "metrics_record", indexes = {
        @Index(name = "idx_metrics_record_call_time", columnList = "callTime"),
        @Index(name = "idx_metrics_record_caller_type", columnList = "callerType"),
        @Index(name = "idx_metrics_record_caller_level", columnList = "callerLevel"),
        @Index(name = "idx_metrics_record_caller_dept", columnList = "callerDept"),
        @Index(name = "idx_metrics_record_api_path", columnList = "apiPath")
})
public class MetricsRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String callerName;

    @Column(nullable = false, length = 50)
    private String callerType;

    @Column(nullable = false, length = 50)
    private String callerLevel;

    @Column(nullable = false, length = 100)
    private String callerDept;

    @Column(nullable = false, length = 200)
    private String apiPath;

    @Column(nullable = false, length = 10)
    private String apiMethod;

    @Column(nullable = false)
    private LocalDateTime callTime;

    @Column(length = 50)
    private String clientIp;

    @Column(length = 500)
    private String userAgent;

    @Column(nullable = false)
    private LocalDateTime gmtCreate;

    @PrePersist
    protected void onCreate() {
        if (callTime == null) callTime = LocalDateTime.now(ZoneOffset.UTC);
        if (gmtCreate == null) gmtCreate = LocalDateTime.now(ZoneOffset.UTC);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCallerName() { return callerName; }
    public void setCallerName(String callerName) { this.callerName = callerName; }
    public String getCallerType() { return callerType; }
    public void setCallerType(String callerType) { this.callerType = callerType; }
    public String getCallerLevel() { return callerLevel; }
    public void setCallerLevel(String callerLevel) { this.callerLevel = callerLevel; }
    public String getCallerDept() { return callerDept; }
    public void setCallerDept(String callerDept) { this.callerDept = callerDept; }
    public String getApiPath() { return apiPath; }
    public void setApiPath(String apiPath) { this.apiPath = apiPath; }
    public String getApiMethod() { return apiMethod; }
    public void setApiMethod(String apiMethod) { this.apiMethod = apiMethod; }
    public LocalDateTime getCallTime() { return callTime; }
    public void setCallTime(LocalDateTime callTime) { this.callTime = callTime; }
    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public LocalDateTime getGmtCreate() { return gmtCreate; }
    public void setGmtCreate(LocalDateTime gmtCreate) { this.gmtCreate = gmtCreate; }
}