package com.orgarch.department;

import com.orgarch.common.BizException;
import com.orgarch.employee.Employee;
import com.orgarch.employee.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DepartmentDeleteTest {

    @Autowired private DepartmentRepository deptRepo;
    @Autowired private EmployeeRepository empRepo;
    @Autowired private DepartmentService deptService;

    @Test
    void delete_emptyDept_marksDeleted() {
        Department d = deptRepo.save(new Department("前端组", null));
        d.setPath("/" + d.getId() + "/");
        deptRepo.save(d);

        deptService.delete(d.getId());

        assertEquals(1, deptRepo.findById(d.getId()).orElseThrow().getIsDeleted());
    }

    @Test
    void delete_deptWithEmployees_isRejected() {
        Department d = deptRepo.save(new Department("前端组", null));
        d.setPath("/" + d.getId() + "/");
        deptRepo.save(d);
        Employee e = new Employee();
        e.setName("张三");
        e.setEmployeeNo("10086");
        e.setPhone("13800138000");
        e.setDeptId(d.getId());
        e.setStatus("ACTIVE");
        e.setIsDeleted(0);
        empRepo.save(e);

        BizException ex = assertThrows(BizException.class, () -> deptService.delete(d.getId()));
        assertEquals(400, ex.getCode());
        assertTrue(ex.getMessage().contains("1"));
    }
}
