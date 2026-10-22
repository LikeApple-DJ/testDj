package com.example.employeeboard.budget;

import com.example.employeeboard.budget.mapper.BudgetMapper;
import com.example.employeeboard.common.BizException;
import com.example.employeeboard.employee.EmployeeDO;
import com.example.employeeboard.employee.mapper.EmployeeMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * BudgetServiceImpl 单元测试。
 */
@ExtendWith(MockitoExtension.class)
class BudgetServiceImplTest {

    @Mock
    private BudgetMapper budgetMapper;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private BudgetServiceImpl budgetService;

    private BudgetSaveReq buildReq() {
        BudgetSaveReq req = new BudgetSaveReq();
        req.setEmpNo("A1001");
        req.setBudgetPeriod("2026-10");
        req.setAmount(new BigDecimal("25000.00"));
        req.setCurrency("CNY");
        req.setRemark("10月人力预算");
        return req;
    }

    @Test
    void should_saveBudget_when_validRequest() {
        when(employeeMapper.selectByEmpNo(anyString(), anyString())).thenReturn(new EmployeeDO());

        boolean result = budgetService.saveBudget(buildReq());

        assertTrue(result);
        verify(budgetMapper).upsert(any(BudgetDO.class));
    }

    @Test
    void should_throwBud001_when_employeeNotFound() {
        when(employeeMapper.selectByEmpNo(anyString(), anyString())).thenReturn(null);

        BizException e = assertThrows(BizException.class, () -> budgetService.saveBudget(buildReq()));
        assertEquals("BUD_001", e.getErrorCode());
    }

    @Test
    void should_throwBud002_when_periodInvalid() {
        when(employeeMapper.selectByEmpNo(anyString(), anyString())).thenReturn(new EmployeeDO());
        BudgetSaveReq req = buildReq();
        req.setBudgetPeriod("2026/10");

        BizException e = assertThrows(BizException.class, () -> budgetService.saveBudget(req));
        assertEquals("BUD_002", e.getErrorCode());
    }

    @Test
    void should_throwBud002_when_amountNegative() {
        when(employeeMapper.selectByEmpNo(anyString(), anyString())).thenReturn(new EmployeeDO());
        BudgetSaveReq req = buildReq();
        req.setAmount(new BigDecimal("-1.00"));

        BizException e = assertThrows(BizException.class, () -> budgetService.saveBudget(req));
        assertEquals("BUD_002", e.getErrorCode());
    }

    @Test
    void should_acceptQuarterPeriod_when_validFormat() {
        when(employeeMapper.selectByEmpNo(anyString(), anyString())).thenReturn(new EmployeeDO());
        BudgetSaveReq req = buildReq();
        req.setBudgetPeriod("2026-Q4");

        assertTrue(budgetService.saveBudget(req));
    }
}
