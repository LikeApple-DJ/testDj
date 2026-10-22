package com.example.employeeboard.budget;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 保存成本预算请求（W10）。
 */
public class BudgetSaveReq {

    @NotBlank(message = "员工工号必填")
    private String empNo;

    /** 预算周期，格式 yyyy-MM 或 yyyy-Qn */
    @NotBlank(message = "预算周期必填")
    private String budgetPeriod;

    @NotNull(message = "金额必填")
    @DecimalMin(value = "0.00", message = "金额不能为负")
    private BigDecimal amount;

    /** 默认 CNY */
    private String currency;

    @Size(max = 256)
    private String remark;

    public String getEmpNo() {
        return empNo;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public String getBudgetPeriod() {
        return budgetPeriod;
    }

    public void setBudgetPeriod(String budgetPeriod) {
        this.budgetPeriod = budgetPeriod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
