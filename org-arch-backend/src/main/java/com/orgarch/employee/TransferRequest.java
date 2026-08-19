package com.orgarch.employee;

import jakarta.validation.constraints.NotNull;

public class TransferRequest {
    @NotNull private Long newDeptId;
    private String newPosition;
    private String reason;

    public Long getNewDeptId() { return newDeptId; }
    public void setNewDeptId(Long newDeptId) { this.newDeptId = newDeptId; }
    public String getNewPosition() { return newPosition; }
    public void setNewPosition(String newPosition) { this.newPosition = newPosition; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
