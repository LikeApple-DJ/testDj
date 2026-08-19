package com.orgarch.approval;

import com.orgarch.department.Department;
import com.orgarch.department.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApprovalFlowService {

    private static final Logger log = LoggerFactory.getLogger(ApprovalFlowService.class);

    private static final String SCENE_LEAVE = "LEAVE";

    private final ApprovalFlowNodeRepository flowRepo;
    private final DepartmentRepository deptRepo;

    public ApprovalFlowService(ApprovalFlowNodeRepository flowRepo,
                               DepartmentRepository deptRepo) {
        this.flowRepo = flowRepo;
        this.deptRepo = deptRepo;
    }

    /**
     * 调动后级联更新默认审批人（R22/R35/R36）：
     * 按目标部门 leader_id 更新 scene=LEAVE 的审批节点 approver_id；
     * 若员工尚无审批节点，则新建一条（design 5.4.2.1）。
     *
     * @param employeeId 调动员工ID
     * @param newDeptId 目标部门ID
     */
    @Transactional
    public void cascadeOnTransfer(Long employeeId, Long newDeptId) {
        Department targetDept = deptRepo.findById(newDeptId)
                .orElseThrow(() -> new IllegalStateException("目标部门不存在: " + newDeptId));
        Long leaderId = targetDept.getLeaderId();
        if (leaderId == null) {
            log.warn("目标部门[id={}] 未配置 leader_id，审批人 approver_id 将置空", newDeptId);
        }

        List<ApprovalFlowNode> nodes = flowRepo.findByEmployeeIdAndScene(employeeId, SCENE_LEAVE);
        if (nodes.isEmpty()) {
            // 员工无审批节点记录，新建一条默认节点
            ApprovalFlowNode node = new ApprovalFlowNode();
            node.setEmployeeId(employeeId);
            node.setDeptId(newDeptId);
            node.setApproverId(leaderId);
            node.setScene(SCENE_LEAVE);
            flowRepo.save(node);
            return;
        }
        for (ApprovalFlowNode node : nodes) {
            node.setDeptId(newDeptId);
            node.setApproverId(leaderId);
            flowRepo.save(node);
        }
    }

    /**
     * 离职时停用该员工作为审批人的节点（R29）：
     * 将 approval_flow_node.approver_id 置空，保留历史记录不物理删除。
     *
     * @param employeeId 离职员工ID
     */
    @Transactional
    public void deactivateApproverNodes(Long employeeId) {
        List<ApprovalFlowNode> nodes = flowRepo.findByApproverId(employeeId);
        for (ApprovalFlowNode node : nodes) {
            node.setApproverId(null);
            flowRepo.save(node);
        }
        log.info("已停用员工[id={}]作为审批人的审批节点共{}条", employeeId, nodes.size());
    }
}
