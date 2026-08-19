package com.orgarch.employee;

import com.orgarch.common.BizException;
import com.orgarch.department.Department;
import com.orgarch.department.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EmployeeCreateTest {

    @Autowired private EmployeeService empService;
    @Autowired private DepartmentRepository deptRepo;

    private Long seedDept(String name) {
        Department d = deptRepo.save(new Department(name, null));
        d.setPath("/" + d.getId() + "/");
        deptRepo.save(d);
        return d.getId();
    }

    @Test
    void create_validPersists() {
        Long deptId = seedDept("研发部");
        EmployeeCreateRequest req = new EmployeeCreateRequest();
        req.setName("张三");
        req.setEmployeeNo("10086");
        req.setPhone("13800138000");
        req.setDeptId(deptId);
        req.setPosition("前端开发");

        EmployeeVo vo = empService.create(req);

        assertNotNull(vo.getId());
        assertEquals("ACTIVE", vo.getStatus());
    }

    @Test
    void create_duplicateEmployeeNo_throws() {
        Long deptId = seedDept("研发部");
        EmployeeCreateRequest req1 = new EmployeeCreateRequest();
        req1.setName("张三");
        req1.setEmployeeNo("10086");
        req1.setPhone("13800138000");
        req1.setDeptId(deptId);
        empService.create(req1);

        EmployeeCreateRequest req2 = new EmployeeCreateRequest();
        req2.setName("李四");
        req2.setEmployeeNo("10086");
        req2.setPhone("13900000000");
        req2.setDeptId(deptId);

        BizException ex = assertThrows(BizException.class, () -> empService.create(req2));
        assertEquals(400, ex.getCode());
    }

    @Test
    void create_invalidDept_throws() {
        EmployeeCreateRequest req = new EmployeeCreateRequest();
        req.setName("张三");
        req.setEmployeeNo("10086");
        req.setPhone("13800138000");
        req.setDeptId(999999L);

        assertThrows(BizException.class, () -> empService.create(req));
    }
}
