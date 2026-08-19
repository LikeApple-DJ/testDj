package com.orgarch.employee;

import com.orgarch.approval.ApprovalFlowNode;
import com.orgarch.approval.ApprovalFlowNodeRepository;
import com.orgarch.department.Department;
import com.orgarch.department.DepartmentRepository;
import com.orgarch.transfer.TransferRecord;
import com.orgarch.transfer.TransferRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EmployeeTransferTest {

    @Autowired private EmployeeService empService;
    @Autowired private EmployeeRepository empRepo;
    @Autowired private DepartmentRepository deptRepo;
    @Autowired private ApprovalFlowNodeRepository flowRepo;
    @Autowired private TransferRecordRepository transferRepo;

    private Long seedDept(String name) {
        Department d = deptRepo.save(new Department(name, null));
        d.setPath("/" + d.getId() + "/");
        deptRepo.save(d);
        return d.getId();
    }

    private Long seedEmployee(Long deptId, String no, String phone) {
        Employee e = new Employee();
        e.setName("张三");
        e.setEmployeeNo(no);
        e.setPhone(phone);
        e.setDeptId(deptId);
        e.setStatus("ACTIVE");
        e.setIsDeleted(0);
        return empRepo.save(e).getId();
    }

    @Test
    void transfer_updatesDeptPositionAndCascadesApprovalFlow() {
        Long fromDept = seedDept("前端组");
        Long toDept = seedDept("后端组");
        Long empId = seedEmployee(fromDept, "10086", "13800138000");

        ApprovalFlowNode node = new ApprovalFlowNode();
        node.setEmployeeId(empId);
        node.setDeptId(fromDept);
        node.setApproverId(100L);
        node.setScene("LEAVE");
        flowRepo.save(node);

        TransferRequest req = new TransferRequest();
        req.setNewDeptId(toDept);
        req.setNewPosition("Java开发");
        req.setReason("业务调整");

        empService.transfer(empId, req);

        Employee updated = empRepo.findById(empId).orElseThrow();
        assertEquals(toDept, updated.getDeptId());
        assertEquals("Java开发", updated.getPosition());

        ApprovalFlowNode updatedNode = flowRepo.findAll().get(0);
        assertEquals(toDept, updatedNode.getDeptId());

        TransferRecord rec = transferRepo.findByEmployeeId(empId).get(0);
        assertEquals(fromDept, rec.getFromDeptId());
        assertEquals(toDept, rec.getToDeptId());
    }

    @Test
    void transfer_concurrent_throwsOptimisticLock() {
        Long fromDept = seedDept("前端组");
        Long toDept = seedDept("后端组");
        Long empId = seedEmployee(fromDept, "10087", "13800138001");

        Employee managed = empRepo.findById(empId).orElseThrow();
        Integer v1 = managed.getVersion();

        // 模拟另一线程已先提交：手动改写版本号制造冲突
        managed.setVersion(v1 + 1); // 通过显式改写触发 save 后的版本比对冲突
        empRepo.saveAndFlush(managed);

        // 用旧版本号上下文再次发起调动
        Employee stale = empRepo.findById(empId).orElseThrow();
        // 还原版本号到旧值以模拟客户端持有的旧版本
        stale.setVersion(v1);

        TransferRequest req = new TransferRequest();
        req.setNewDeptId(toDept);
        req.setNewPosition("Java开发");
        req.setReason("业务调整");

        assertThrows(ObjectOptimisticLockingFailureException.class,
                () -> empService.transferWithVersion(empId, req, v1));
    }
}
