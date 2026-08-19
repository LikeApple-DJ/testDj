package com.orgarch.approval;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalFlowNodeRepository extends JpaRepository<ApprovalFlowNode, Long> {

    /** 查询某员工所有审批节点。 */
    List<ApprovalFlowNode> findByEmployeeId(Long employeeId);

    /** 查询某员工指定场景的审批节点（如 LEAVE 请假场景）。 */
    List<ApprovalFlowNode> findByEmployeeIdAndScene(Long employeeId, String scene);

    /** 查询某员工作为审批人的所有节点（用于离职时停用，R29）。 */
    List<ApprovalFlowNode> findByApproverId(Long approverId);
}
