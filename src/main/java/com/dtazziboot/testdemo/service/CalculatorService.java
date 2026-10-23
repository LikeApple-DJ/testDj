package com.dtazziboot.testdemo.service;

import com.dtazziboot.testdemo.model.dto.CalculatorVO;
import java.math.BigDecimal;

/**
 * 计算器服务接口
 */
public interface CalculatorService {

    /**
     * 执行计算
     *
     * @param operation 操作类型 ADD / SUBTRACT / MULTIPLY / DIVIDE
     * @param operandA  操作数A
     * @param operandB  操作数B
     * @return 计算响应
     */
    CalculatorVO calculate(String operation, BigDecimal operandA, BigDecimal operandB);
}