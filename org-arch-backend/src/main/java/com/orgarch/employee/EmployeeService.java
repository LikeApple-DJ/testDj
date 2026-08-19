package com.orgarch.employee;

import com.orgarch.approval.ApprovalFlowService;
import com.orgarch.common.BizException;
import com.orgarch.department.Department;
import com.orgarch.department.DepartmentRepository;
import com.orgarch.transfer.TransferRecord;
import com.orgarch.transfer.TransferRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    /** 分页 size 上限（R16/A07），防止全量拉取。 */
    private static final int MAX_PAGE_SIZE = 100;

    private static final String STATUS_ACTIVE = "ACTIVE";
    private static final String STATUS_RESIGNED = "RESIGNED";
    private static final String DEFAULT_OPERATOR = "SYSTEM";

    private final EmployeeRepository empRepo;
    private final DepartmentRepository deptRepo;
    private final TransferRecordRepository transferRepo;
    private final ApprovalFlowService approvalFlowService;

    public EmployeeService(EmployeeRepository empRepo,
                           DepartmentRepository deptRepo,
                           TransferRecordRepository transferRepo,
                           ApprovalFlowService approvalFlowService) {
        this.empRepo = empRepo;
        this.deptRepo = deptRepo;
        this.transferRepo = transferRepo;
        this.approvalFlowService = approvalFlowService;
    }

    /**
     * 工号/手机号实时唯一性校验（R11/R12）。
     * 对 field 做白名单校验，非法字段返回 400（R39）而非抛 IllegalArgumentException 致 500。
     */
    @Transactional(readOnly = true)
    public Map<String, Object> check(String field, String value) {
        if (field == null || value == null) {
            throw new BizException(400, "校验字段与值不能为空");
        }
        boolean exist;
        switch (field) {
            case "employeeNo" -> exist = empRepo.existsByEmployeeNoAndIsDeleted(value, 0);
            case "phone" -> exist = empRepo.existsByPhoneAndIsDeleted(value, 0);
            default -> throw new BizException(400, "不支持的校验字段: " + field);
        }
        return Map.of("isExist", exist);
    }

    /**
     * 员工新增（F02/R13）：
     * 1) 校验所属部门存在；
     * 2) 应用层二次唯一性校验，DB 唯一索引保底；
     * 3) 并发新增导致 DB 索引冲突时，由 GlobalExceptionHandler 捕获
     *    DataIntegrityViolationException 返回 400 + EMP_002（design 5.2.3.1）。
     */
    @Transactional
    public EmployeeVo create(EmployeeCreateRequest req) {
        // 部门合法性（R13）
        deptRepo.findById(req.getDeptId())
                .filter(d -> d.getIsDeleted() == 0)
                .orElseThrow(() -> new BizException(400, "所属部门不存在"));

        // 应用层二次唯一性校验（R11/R12），数据库唯一索引保底
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
        e.setStatus(STATUS_ACTIVE);
        e.setIsDeleted(0);
        return new EmployeeVo(empRepo.save(e));
    }

    /**
     * 人员调动（F03/R14/R22/R23/R34）：更新 dept_id、级联审批流、留痕。
     * operator 优先取请求头 X-Operator，缺失时降级为 SYSTEM（A04）。
     */
    @Transactional
    public EmployeeVo transfer(Long id, TransferRequest req) {
        return transfer(id, req, null);
    }

    @Transactional
    public EmployeeVo transfer(Long id, TransferRequest req, String operator) {
        Department target = deptRepo.findById(req.getNewDeptId())
                .filter(d -> d.getIsDeleted() == 0)
                .orElseThrow(() -> new BizException(400, "目标部门不存在"));
        Employee e = empRepo.findByIdAndIsDeleted(id, 0)
                .orElseThrow(() -> new BizException(400, "员工不存在"));
        if (!STATUS_ACTIVE.equals(e.getStatus())) {
            throw new BizException(400, "离职员工不可调动");
        }

        Long fromDept = e.getDeptId();
        String oldPosition = e.getPosition();
        e.setDeptId(target.getId());
        e.setPosition(req.getNewPosition());
        Employee saved = empRepo.save(e); // 触发 @Version 乐观锁（R15）

        // R22/R35：按目标部门 leader_id 级联更新默认审批人
        approvalFlowService.cascadeOnTransfer(id, target.getId());

        // R23/R34：调动留痕，operator 优先取请求头 X-Operator
        TransferRecord rec = new TransferRecord();
        rec.setEmployeeId(id);
        rec.setFromDeptId(fromDept);
        rec.setToDeptId(target.getId());
        rec.setOldPosition(oldPosition);
        rec.setNewPosition(req.getNewPosition());
        rec.setReason(req.getReason());
        rec.setOperator(operator != null && !operator.isBlank() ? operator : DEFAULT_OPERATOR);
        transferRepo.save(rec);

        return new EmployeeVo(saved);
    }

    /**
     * 员工离职（F04/R25-R30）：逻辑删除 + 状态隔离 + 资源释放。
     * 使用 findById 以检测"已离职"状态（R26/R27），返回 EMP_006 语义而非笼统的"不存在"。
     */
    @Transactional
    public EmployeeVo resign(Long id, ResignRequest req) {
        Employee e = empRepo.findById(id)
                .orElseThrow(() -> new BizException(400, "员工不存在"));
        if (!STATUS_ACTIVE.equals(e.getStatus())) {
            throw new BizException(400, "该员工已离职，不可重复办理");
        }
        e.setStatus(STATUS_RESIGNED);
        e.setIsDeleted(1);
        e.setResignDate(req.getResignDate());
        Employee saved = empRepo.save(e);
        releaseSystemResources(id);
        return new EmployeeVo(saved);
    }

    /**
     * 离职资源释放（R29/R30）：
     * 1) 停用该员工作为 approver 的审批节点（approval_flow_node.approver_id 置空）；
     * 2) 预留外部 IAM 账号许可/登录权限释放扩展点（调用失败降级，不阻断离职主流程）。
     */
    private void releaseSystemResources(Long employeeId) {
        approvalFlowService.deactivateApproverNodes(employeeId);
        // 下游 IAM/许可系统接口占位：调用 /iam/licenses/release?employeeId=...
        // 当前无实际 IAM 对接，降级为日志；正式接入后由网关注入身份并调用外部 IAM API。
        log.info("[OrgArch] release license & login permission for employee[id={}]", employeeId);
    }

    /**
     * 员工分页查询（R16/A07）：size 超过 100 强制截断，防止全量拉取。
     */
    @Transactional(readOnly = true)
    public EmployeePageVo page(Long deptId, String status, int page, int size) {
        int cappedSize = Math.min(size, MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), cappedSize);
        Page<Employee> p = empRepo.queryPage(deptId, status, pageable);
        List<EmployeeVo> list = p.getContent().stream()
                .map(EmployeeVo::new)
                .toList();
        return new EmployeePageVo(page, cappedSize, p.getTotalElements(), list);
    }
}
