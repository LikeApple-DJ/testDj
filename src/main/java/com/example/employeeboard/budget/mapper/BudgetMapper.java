package com.example.employeeboard.budget.mapper;

import com.example.employeeboard.budget.BudgetDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 成本预算数据访问层。
 */
@Mapper
public interface BudgetMapper {

    /**
     * upsert：同员工同周期重复保存 = 覆盖更新（唯一索引 uk_emp_budget_emp_period 兜底）。
     */
    @Insert("INSERT INTO emp_budget (tenant_id, emp_no, budget_period, amount, currency, remark) "
            + "VALUES (#{tenantId}, #{empNo}, #{budgetPeriod}, #{amount}, #{currency}, #{remark}) "
            + "ON DUPLICATE KEY UPDATE amount = VALUES(amount), currency = VALUES(currency), "
            + "remark = VALUES(remark), gmt_modified = NOW()")
    int upsert(BudgetDO record);

    @Select("SELECT * FROM emp_budget WHERE tenant_id = #{tenantId} AND emp_no = #{empNo} "
            + "ORDER BY budget_period DESC")
    List<BudgetDO> selectByEmpNo(@Param("tenantId") String tenantId, @Param("empNo") String empNo);

    @Select("SELECT IFNULL(SUM(amount), 0) FROM emp_budget "
            + "WHERE tenant_id = #{tenantId} AND budget_period = #{period}")
    BigDecimal sumByPeriod(@Param("tenantId") String tenantId, @Param("period") String period);
}
