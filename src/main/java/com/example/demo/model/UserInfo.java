package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "caller_id", unique = true, nullable = false)
    private String callerId;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "caller_type")
    private String callerType;

    @Column(name = "caller_level")
    private String callerLevel;

    @Column(name = "caller_dept")
    private String callerDept;

    public UserInfo() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCallerId() { return callerId; }
    public void setCallerId(String callerId) { this.callerId = callerId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getCallerType() { return callerType; }
    public void setCallerType(String callerType) { this.callerType = callerType; }

    public String getCallerLevel() { return callerLevel; }
    public void setCallerLevel(String callerLevel) { this.callerLevel = callerLevel; }

    public String getCallerDept() { return callerDept; }
    public void setCallerDept(String callerDept) { this.callerDept = callerDept; }
}