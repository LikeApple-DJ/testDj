package com.orgarch.employee;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EmployeePageTest {

    @Autowired private EmployeeService empService;
    @Autowired private EmployeeRepository empRepo;

    @Test
    void page_returnsPagedResult() {
        for (int i = 0; i < 15; i++) {
            Employee e = new Employee();
            e.setName("员工" + i);
            e.setEmployeeNo("N" + i);
            e.setPhone("1300000000" + i);
            e.setDeptId(1L);
            e.setStatus("ACTIVE");
            e.setIsDeleted(0);
            empRepo.save(e);
        }

        EmployeePageVo page1 = empService.page(1L, "ACTIVE", 1, 10);

        assertEquals(1, page1.getPage());
        assertEquals(10, page1.getSize());
        assertEquals(15L, page1.getTotal());
        assertEquals(10, page1.getList().size());
    }

    @Test
    void page_filterByStatus() {
        Employee a = new Employee();
        a.setName("在职"); a.setEmployeeNo("A1"); a.setPhone("1001"); a.setDeptId(1L);
        a.setStatus("ACTIVE"); a.setIsDeleted(0);
        empRepo.save(a);
        Employee r = new Employee();
        r.setName("离职"); r.setEmployeeNo("A2"); r.setPhone("1002"); r.setDeptId(1L);
        r.setStatus("RESIGNED"); r.setIsDeleted(1);
        empRepo.save(r);

        EmployeePageVo result = empService.page(1L, "ACTIVE", 1, 10);

        assertEquals(1, result.getList().size());
        assertEquals("在职", result.getList().get(0).getName());
    }
}
