package com.org.module.event;

import lombok.Getter;

@Getter
public class EmployeeTransferredEvent {
    private final Long employeeId;
    private final Long oldDeptId;
    private final Long newDeptId;

    public EmployeeTransferredEvent(Long employeeId, Long oldDeptId, Long newDeptId) {
        this.employeeId = employeeId;
        this.oldDeptId = oldDeptId;
        this.newDeptId = newDeptId;
    }
}
