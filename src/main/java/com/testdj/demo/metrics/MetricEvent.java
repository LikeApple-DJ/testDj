package com.testdj.demo.metrics;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class MetricEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String traceId;
    private String userId;
    private String userType;
    private String userLevel;
    private String userDept;
    private String api;
    private Instant timestamp;

    public MetricEvent() {
    }

    public MetricEvent(String traceId, String userId, String userType, String userLevel, String userDept, String api, Instant timestamp) {
        this.traceId = traceId;
        this.userId = userId;
        this.userType = userType;
        this.userLevel = userLevel;
        this.userDept = userDept;
        this.api = api;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    public String getUserLevel() { return userLevel; }
    public void setUserLevel(String userLevel) { this.userLevel = userLevel; }
    public String getUserDept() { return userDept; }
    public void setUserDept(String userDept) { this.userDept = userDept; }
    public String getApi() { return api; }
    public void setApi(String api) { this.api = api; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}