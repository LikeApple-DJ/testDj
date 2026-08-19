package com.orgarch.transfer;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfer_record")
public class TransferRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "from_dept_id", nullable = false)
    private Long fromDeptId;

    @Column(name = "to_dept_id", nullable = false)
    private Long toDeptId;

    @Column(name = "old_position", length = 50)
    private String oldPosition;

    @Column(name = "new_position", length = 50)
    private String newPosition;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "operator", length = 50)
    private String operator;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public Long getFromDeptId() { return fromDeptId; }
    public void setFromDeptId(Long fromDeptId) { this.fromDeptId = fromDeptId; }
    public Long getToDeptId() { return toDeptId; }
    public void setToDeptId(Long toDeptId) { this.toDeptId = toDeptId; }
    public String getOldPosition() { return oldPosition; }
    public void setOldPosition(String oldPosition) { this.oldPosition = oldPosition; }
    public String getNewPosition() { return newPosition; }
    public void setNewPosition(String newPosition) { this.newPosition = newPosition; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
