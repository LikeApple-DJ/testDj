package com.example.demo.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
@Entity
@Table(name = "tracking_records")
public class TrackingRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "api_name", length = 50)
    private String apiName;
    @Column(name = "params_json", columnDefinition = "TEXT")
    private String paramsJson;
    @Column(name = "call_time")
    private LocalDateTime callTime = LocalDateTime.now(ZoneOffset.UTC);
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    public TrackingRecord() {}
    public TrackingRecord(Long userId, String apiName, String paramsJson, String ipAddress) {
        this.userId = userId; this.apiName = apiName; this.paramsJson = paramsJson; this.ipAddress = ipAddress;
    }
    // Getters and setters
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public String getApiName() { return apiName; } public void setApiName(String apiName) { this.apiName = apiName; }
    public String getParamsJson() { return paramsJson; } public void setParamsJson(String paramsJson) { this.paramsJson = paramsJson; }
    public LocalDateTime getCallTime() { return callTime; } public void setCallTime(LocalDateTime callTime) { this.callTime = callTime; }
    public String getIpAddress() { return ipAddress; } public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}