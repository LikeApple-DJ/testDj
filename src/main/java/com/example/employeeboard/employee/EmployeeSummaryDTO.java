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
    private RecentImport recentImport;

    /**
     * 最近一次导入批次摘要（W06 出参）。
     */
    public static class RecentImport {

        /** 批次号（emp_import_task.id） */
        private Long batchNo;
        /** 成功数 */
        private Integer successCount;
        /** 失败数 */
        private Integer failCount;

        public RecentImport() {
        }

        public RecentImport(Long batchNo, Integer successCount, Integer failCount) {
            this.batchNo = batchNo;
            this.successCount = successCount;
            this.failCount = failCount;
        }

        public Long getBatchNo() {
            return batchNo;
        }

        public void setBatchNo(Long batchNo) {
            this.batchNo = batchNo;
        }

        public Integer getSuccessCount() {
            return successCount;
        }

        public void setSuccessCount(Integer successCount) {
            this.successCount = successCount;
        }

        public Integer getFailCount() {
            return failCount;
        }

        public void setFailCount(Integer failCount) {
            this.failCount = failCount;
        }
    }

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

    public RecentImport getRecentImport() {
        return recentImport;
    }

    public void setRecentImport(RecentImport recentImport) {
        this.recentImport = recentImport;
    }
}
