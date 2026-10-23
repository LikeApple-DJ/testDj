package com.dtazziboot.testdemo.model.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 计算请求
 */
public class CalculatorRequest {

    @NotBlank(message = "操作类型不能为空")
    private String operation;

    private String operandA;

    private String operandB;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperandA() {
        return operandA;
    }

    public void setOperandA(String operandA) {
        this.operandA = operandA;
    }

    public String getOperandB() {
        return operandB;
    }

    public void setOperandB(String operandB) {
        this.operandB = operandB;
    }
}