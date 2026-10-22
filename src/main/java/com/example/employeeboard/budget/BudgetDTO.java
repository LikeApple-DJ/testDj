package com.example.employeeboard.budget;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 成本预算视图对象。
 */
public class BudgetDTO {

    private String budgetPeriod;
    private BigDecimal amount;
    private String currency;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtModified;

    public static BudgetDTO fromDO(BudgetDO record) {
        BudgetDTO dto = new BudgetDTO();
        dto.setBudgetPeriod(record.getBudgetPeriod());
        dto.setAmount(record.getAmount());
        dto.setCurrency(record.getCurrency());
        dto.setRemark(record.getRemark());
        dto.setGmtModified(record.getGmtModified());
        return dto;
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

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
}
