package com.dtazziboot.testdemo.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 计算记录数据对象
 */
public class CalculatorDO {

    private Long id;
    private String operation;
    private BigDecimal operandA;
    private BigDecimal operandB;
    private BigDecimal result;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public BigDecimal getOperandA() {
        return operandA;
    }

    public void setOperandA(BigDecimal operandA) {
        this.operandA = operandA;
    }

    public BigDecimal getOperandB() {
        return operandB;
    }

    public void setOperandB(BigDecimal operandB) {
        this.operandB = operandB;
    }

    public BigDecimal getResult() {
        return result;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Override
    public String toString() {
        return "CalculatorDO{id=" + id + ", operation='" + operation + '\''
                + ", operandA=" + operandA + ", operandB=" + operandB
                + ", result=" + result + '}';
    }
}