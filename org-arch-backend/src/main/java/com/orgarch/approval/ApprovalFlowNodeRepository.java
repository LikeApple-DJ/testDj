package com.orgarch.approval;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalFlowNodeRepository extends JpaRepository<ApprovalFlowNode, Long> {
    List<ApprovalFlowNode> findByEmployeeId(Long employeeId);
}
