package com.dtazziboot.testdemo.model.dto;

import com.dtazziboot.testdemo.model.entity.CalculatorDO;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 计算响应
 */
public class CalculatorVO {

    private String operation;
    private String operandA;
    private String operandB;
    private String result;

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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    /**
     * 从DO构建VO
     */
    public static CalculatorVO from(CalculatorDO calcDO) {
        CalculatorVO vo = new CalculatorVO();
        vo.setOperation(calcDO.getOperation());
        vo.setOperandA(calcDO.getOperandA() != null
                ? calcDO.getOperandA().stripTrailingZeros().toPlainString() : null);
        vo.setOperandB(calcDO.getOperandB() != null
                ? calcDO.getOperandB().stripTrailingZeros().toPlainString() : null);
        vo.setResult(calcDO.getResult() != null
                ? calcDO.getResult().setScale(4, RoundingMode.HALF_UP)
                    .stripTrailingZeros().toPlainString() : null);
        return vo;
    }
}