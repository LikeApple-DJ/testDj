package com.example.employeeboard.employee;

import java.math.BigDecimal;

/**
 * 看板汇总统计（W06）。
 */
public class EmployeeSummaryDTO {

    /** 在职员工总数 */
    private long totalCount;
    /** 部门数 */
    private long deptCount;
    /** 当前周期预算合计 */
    private BigDecimal budgetTotal;
    /** 最近一次导入批次摘要 */
    private Object recentImport;

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public long getDeptCount() {
        return deptCount;
    }

    public void setDeptCount(long deptCount) {
        this.deptCount = deptCount;
    }

    public BigDecimal getBudgetTotal() {
        return budgetTotal;
    }

    public void setBudgetTotal(BigDecimal budgetTotal) {
        this.budgetTotal = budgetTotal;
    }

    public Object getRecentImport() {
        return recentImport;
    }

    public void setRecentImport(Object recentImport) {
        this.recentImport = recentImport;
    }
}
