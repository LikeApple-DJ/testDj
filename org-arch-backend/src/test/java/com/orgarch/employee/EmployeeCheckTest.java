package com.orgarch.employee;

import com.orgarch.common.BizException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EmployeeCheckTest {

    @Autowired private EmployeeRepository empRepo;
    @Autowired private EmployeeService empService;

    @Test
    void check_employeeNo_notExist_returnsFalse() {
        Map<String, Object> r = empService.check("employeeNo", "10086");
        assertEquals(false, r.get("isExist"));
    }

    @Test
    void check_employeeNo_exist_returnsTrue() {
        Employee e = new Employee();
        e.setName("张三");
        e.setEmployeeNo("10086");
        e.setPhone("13800138000");
        e.setDeptId(1L);
        e.setStatus("ACTIVE");
        e.setIsDeleted(0);
        empRepo.save(e);

        Map<String, Object> r = empService.check("employeeNo", "10086");
        assertEquals(true, r.get("isExist"));
    }

    @Test
    void check_phone_exist_returnsTrue() {
        Employee e = new Employee();
        e.setName("李四");
        e.setEmployeeNo("10010");
        e.setPhone("13900000000");
        e.setDeptId(1L);
        e.setStatus("ACTIVE");
        e.setIsDeleted(0);
        empRepo.save(e);

        Map<String, Object> r = empService.check("phone", "13900000000");
        assertEquals(true, r.get("isExist"));
    }

    @Test
    void check_unknownField_throws400() {
        BizException ex = assertThrows(BizException.class,
                () -> empService.check("xxx", "1"));
        assertEquals(400, ex.getCode());
    }
}
