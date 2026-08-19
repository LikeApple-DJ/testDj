package com.orgarch.employee;

import com.orgarch.common.BizException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class EmployeeService {

    private final EmployeeRepository empRepo;
    private final com.orgarch.department.DepartmentRepository deptRepo;
    private final com.orgarch.transfer.TransferRecordRepository transferRepo;
    private final com.orgarch.approval.ApprovalFlowService approvalFlowService;

    public EmployeeService(EmployeeRepository empRepo,
                           com.orgarch.department.DepartmentRepository deptRepo,
                           com.orgarch.transfer.TransferRecordRepository transferRepo,
                           com.orgarch.approval.ApprovalFlowService approvalFlowService) {
        this.empRepo = empRepo;
        this.deptRepo = deptRepo;
        this.transferRepo = transferRepo;
        this.approvalFlowService = approvalFlowService;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> check(String field, String value) {
        boolean exist;
        switch (field) {
            case "employeeNo" -> exist = empRepo.existsByEmployeeNoAndIsDeleted(value, 0);
            case "phone" -> exist = empRepo.existsByPhoneAndIsDeleted(value, 0);
            default -> throw new IllegalArgumentException("不支持的校验字段: " + field);
        }
        return Map.of("isExist", exist);
    }

    @Transactional
    public EmployeeVo create(EmployeeCreateRequest req) {
        // 部门合法性
        deptRepo.findById(req.getDeptId())
                .filter(d -> d.getIsDeleted() == 0)
                .orElseThrow(() -> new BizException(400, "所属部门不存在"));

        // 应用层二次唯一性校验（数据库唯一索引保底）
        if (empRepo.existsByEmployeeNoAndIsDeleted(req.getEmployeeNo(), 0)) {
            throw new BizException(400, "工号已存在: " + req.getEmployeeNo());
        }
        if (empRepo.existsByPhoneAndIsDeleted(req.getPhone(), 0)) {
            throw new BizException(400, "手机号已存在: " + req.getPhone());
        }

        Employee e = new Employee();
        e.setName(req.getName());
        e.setEmployeeNo(req.getEmployeeNo());
        e.setPhone(req.getPhone());
        e.setDeptId(req.getDeptId());
        e.setPosition(req.getPosition());
        e.setStatus("ACTIVE");
        e.setIsDeleted(0);
        return new EmployeeVo(empRepo.save(e));
    }

    @Transactional
    public EmployeeVo transfer(Long id, TransferRequest req) {
        com.orgarch.department.Department target = deptRepo.findById(req.getNewDeptId())
                .filter(d -> d.getIsDeleted() == 0)
                .orElseThrow(() -> new BizException(400, "目标部门不存在"));
        Employee e = empRepo.findByIdAndIsDeleted(id, 0)
                .orElseThrow(() -> new BizException(400, "员工不存在"));
        if (!"ACTIVE".equals(e.getStatus())) {
            throw new BizException(400, "离职员工不可调动");
        }

        Long fromDept = e.getDeptId();
        String oldPosition = e.getPosition();
        e.setDeptId(target.getId());
        e.setPosition(req.getNewPosition());
        Employee saved = empRepo.save(e); // 触发 @Version 乐观锁

        approvalFlowService.cascadeOnTransfer(id, target.getId());

        com.orgarch.transfer.TransferRecord rec = new com.orgarch.transfer.TransferRecord();
        rec.setEmployeeId(id);
        rec.setFromDeptId(fromDept);
        rec.setToDeptId(target.getId());
        rec.setOldPosition(oldPosition);
        rec.setNewPosition(req.getNewPosition());
        rec.setReason(req.getReason());
        rec.setOperator("SYSTEM");
        transferRepo.save(rec);

        return new EmployeeVo(saved);
    }

    /**
     * 供测试模拟并发：客户端携带旧 version，提交后由 JPA @Version 触发冲突。
     */
    @Transactional
    public EmployeeVo transferWithVersion(Long id, TransferRequest req, Integer clientVersion) {
        Employee e = empRepo.findByIdAndIsDeleted(id, 0)
                .orElseThrow(() -> new BizException(400, "员工不存在"));
        if (!e.getVersion().equals(clientVersion)) {
            throw new org.springframework.orm.ObjectOptimisticLockingFailureException(
                    Employee.class, id, null);
        }
        e.setVersion(clientVersion);
        return transfer(id, req);
    }

    @Transactional
    public EmployeeVo resign(Long id, ResignRequest req) {
        Employee e = empRepo.findById(id)
                .orElseThrow(() -> new BizException(400, "员工不存在"));
        if (!"ACTIVE".equals(e.getStatus())) {
            throw new BizException(400, "该员工已离职，不可重复办理");
        }
        e.setStatus("RESIGNED");
        e.setIsDeleted(1);
        e.setResignDate(req.getResignDate());
        Employee saved = empRepo.save(e);
        releaseSystemResources(id);
        return new EmployeeVo(saved);
    }

    private void releaseSystemResources(Long employeeId) {
        // 下游 IAM/许可系统接口占位：调用 /iam/licenses/release?employeeId=...
        // 本模块保证逻辑删除先行；历史考勤/审批数据保留，关联查询带状态标识。
        System.out.println("[OrgArch] release license & login permission for employee " + employeeId);
    }

    @Transactional(readOnly = true)
    public EmployeePageVo page(Long deptId, String status, int page, int size) {
        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(Math.max(page - 1, 0), size);
        org.springframework.data.domain.Page<Employee> p =
                empRepo.queryPage(deptId, status, pageable);
        java.util.List<EmployeeVo> list = p.getContent().stream()
                .map(EmployeeVo::new)
                .toList();
        return new EmployeePageVo(page, size, p.getTotalElements(), list);
    }
}
