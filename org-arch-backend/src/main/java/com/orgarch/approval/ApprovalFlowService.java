package com.orgarch.approval;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApprovalFlowService {

    private final ApprovalFlowNodeRepository flowRepo;

    public ApprovalFlowService(ApprovalFlowNodeRepository flowRepo) {
        this.flowRepo = flowRepo;
    }

    @Transactional
    public void cascadeOnTransfer(Long employeeId, Long newDeptId) {
        List<ApprovalFlowNode> nodes = flowRepo.findByEmployeeId(employeeId);
        for (ApprovalFlowNode node : nodes) {
            node.setDeptId(newDeptId); // 调动后审批人上下文跟随新部门
            flowRepo.save(node);
        }
    }
}
