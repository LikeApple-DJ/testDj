package com.example.employeeboard.employee;

import com.example.employeeboard.budget.BudgetService;
import com.example.employeeboard.common.BizException;
import com.example.employeeboard.common.PageResult;
import com.example.employeeboard.employee.mapper.EmployeeMapper;
import com.example.employeeboard.importing.ImportTaskDO;
import com.example.employeeboard.importing.mapper.ImportTaskMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

/**
 * EmployeeServiceImpl 单元测试。
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private BudgetService budgetService;

    @Mock
    private ImportTaskMapper importTaskMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private EmployeeCreateReq buildCreateReq() {
        EmployeeCreateReq req = new EmployeeCreateReq();
        req.setEmpNo("A1001");
        req.setEmpName("张三");
        req.setDeptName("技术部");
        req.setEmail("zhangsan@example.com");
        req.setPhone("13800000000");
        req.setHireDate("2026-10-22");
        return req;
    }

    @Test
    void should_returnDTO_when_createWithValidRequest() {
        when(employeeMapper.selectByEmpNo(anyString(), anyString())).thenReturn(null);

        EmployeeDTO dto = employeeService.create(buildCreateReq());

        assertNotNull(dto);
        assertEquals("A1001", dto.getEmpNo());
        assertEquals("ACTIVE", dto.getStatus());
    }

    @Test
    void should_throwEmp001_when_empNoExists() {
        when(employeeMapper.selectByEmpNo(anyString(), anyString())).thenReturn(new EmployeeDO());

        BizException e = assertThrows(BizException.class, () -> employeeService.create(buildCreateReq()));
        assertEquals("EMP_001", e.getErrorCode());
    }

    @Test
    void should_throwEmp002_when_emailInvalid() {
        EmployeeCreateReq req = buildCreateReq();
        req.setEmail("not-an-email");

        BizException e = assertThrows(BizException.class, () -> employeeService.create(req));
        assertEquals("EMP_002", e.getErrorCode());
    }

    @Test
    void should_throwEmp002_when_hireDateInvalid() {
        when(employeeMapper.selectByEmpNo(anyString(), anyString())).thenReturn(null);
        EmployeeCreateReq req = buildCreateReq();
        req.setHireDate("2026-13-40");

        BizException e = assertThrows(BizException.class, () -> employeeService.create(req));
        assertEquals("EMP_002", e.getErrorCode());
    }

    @Test
    void should_throwEmp003_when_updateNotFound() {
        when(employeeMapper.selectByEmpNo(anyString(), anyString())).thenReturn(null);
        EmployeeUpdateReq req = new EmployeeUpdateReq();
        req.setEmpNo("A9999");

        BizException e = assertThrows(BizException.class, () -> employeeService.update(req));
        assertEquals("EMP_003", e.getErrorCode());
    }

    @Test
    void should_throwEmp004_when_statusInvalid() {
        EmployeeDO existing = new EmployeeDO();
        existing.setId(1L);
        when(employeeMapper.selectByEmpNo(anyString(), anyString())).thenReturn(existing);
        EmployeeUpdateReq req = new EmployeeUpdateReq();
        req.setEmpNo("A1001");
        req.setStatus("UNKNOWN");

        BizException e = assertThrows(BizException.class, () -> employeeService.update(req));
        assertEquals("EMP_004", e.getErrorCode());
    }

    @Test
    void should_returnMaskedPhone_when_pageQueryWithMask() {
        EmployeeDO record = new EmployeeDO();
        record.setEmpNo("A1001");
        record.setPhone("13800000000");
        when(employeeMapper.countPage(anyString(), isNull(), isNull(), isNull())).thenReturn(1L);
        when(employeeMapper.selectPage(anyString(), isNull(), isNull(), isNull(), anyInt(), anyInt()))
                .thenReturn(List.of(record));
        EmployeeQueryReq req = new EmployeeQueryReq();
        req.setPageNo(1);
        req.setPageSize(20);

        PageResult<EmployeeDTO> page = employeeService.pageQuery(req, true);

        assertEquals(1L, page.getTotal());
        assertEquals("138****0000", page.getList().get(0).getPhone());
    }

    @Test
    void should_fillRecentImport_when_summaryHasLatestTask() {
        when(employeeMapper.countActive(anyString())).thenReturn(3L);
        when(employeeMapper.countDepts(anyString())).thenReturn(2L);
        when(budgetService.sumCurrentPeriod()).thenReturn(BigDecimal.TEN);
        ImportTaskDO task = new ImportTaskDO();
        task.setId(7L);
        task.setSuccessCount(9);
        task.setFailCount(1);
        when(importTaskMapper.selectLatestTask(anyString())).thenReturn(task);

        EmployeeSummaryDTO summary = employeeService.summary();

        assertNotNull(summary.getRecentImport());
        assertEquals(7L, summary.getRecentImport().getBatchNo());
        assertEquals(9, summary.getRecentImport().getSuccessCount());
        assertEquals(1, summary.getRecentImport().getFailCount());
    }

    @Test
    void should_throwEmp003_when_deleteNotFound() {
        when(employeeMapper.selectByEmpNo(anyString(), anyString())).thenReturn(null);

        BizException e = assertThrows(BizException.class, () -> employeeService.deleteByEmpNo("A9999"));
        assertEquals("EMP_003", e.getErrorCode());
    }
}
