package com.example.employeeboard.budget;

import java.math.BigDecimal;
import java.util.List;

/**
 * 成本预算服务。
 */
public interface BudgetService {

    /**
     * 保存预算（S09），同员工同周期覆盖更新。
     */
    boolean saveBudget(BudgetSaveReq req);

    /**
     * 员工预算查询（S10），按周期倒序。
     */
    List<BudgetDTO> queryByEmpNo(String empNo);

    /**
     * 当前周期（yyyy-MM）预算合计，供看板汇总使用。
     */
    BigDecimal sumCurrentPeriod();
}
