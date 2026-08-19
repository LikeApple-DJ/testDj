package com.orgarch.employee;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EmployeeResignTest {

    @Autowired private EmployeeService empService;
    @Autowired private EmployeeRepository empRepo;

    @Test
    void resign_setsStatusAndLogicalDelete() {
        Employee e = new Employee();
        e.setName("张三");
        e.setEmployeeNo("10086");
        e.setPhone("13800138000");
        e.setDeptId(1L);
        e.setStatus("ACTIVE");
        e.setIsDeleted(0);
        Long id = empRepo.save(e).getId();

        ResignRequest req = new ResignRequest();
        req.setResignDate(LocalDate.of(2023, 11, 1));

        empService.resign(id, req);

        Employee after = empRepo.findById(id).orElseThrow();
        assertEquals("RESIGNED", after.getStatus());
        assertEquals(1, after.getIsDeleted());
        assertEquals(LocalDate.of(2023, 11, 1), after.getResignDate());
    }

    @Test
    void resign_alreadyResigned_throws() {
        Employee e = new Employee();
        e.setName("张三");
        e.setEmployeeNo("10087");
        e.setPhone("13800138001");
        e.setDeptId(1L);
        e.setStatus("RESIGNED");
        e.setIsDeleted(1);
        Long id = empRepo.save(e).getId();

        ResignRequest req = new ResignRequest();
        req.setResignDate(LocalDate.of(2023, 11, 1));

        assertThrows(com.orgarch.common.BizException.class, () -> empService.resign(id, req));
    }
}
