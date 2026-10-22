package com.dtazziboot.testdemo.service.impl;

import com.dtazziboot.testdemo.common.constant.CalculatorConstants;
import com.dtazziboot.testdemo.common.exception.BusinessException;
import com.dtazziboot.testdemo.model.dto.CalculatorVO;
import com.dtazziboot.testdemo.model.entity.CalculatorDO;
import com.dtazziboot.testdemo.service.CalculatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * 计算器服务实现
 */
@Service
public class CalculatorServiceImpl implements CalculatorService {

    private static final Logger logger = LoggerFactory.getLogger(CalculatorServiceImpl.class);

    @Override
    public CalculatorVO calculate(String operation, BigDecimal operandA, BigDecimal operandB) {
        // 参数校验
        if (operation == null || operation.trim().isEmpty()) {
            throw new BusinessException("B0001", "操作类型不能为空");
        }
        if (operandA == null || operandB == null) {
            throw new BusinessException("B0002", "操作数不能为空");
        }

        String op = operation.trim().toUpperCase();
        BigDecimal result;

        switch (op) {
            case CalculatorConstants.OP_ADD:
                result = operandA.add(operandB);
                break;
            case CalculatorConstants.OP_SUBTRACT:
                result = operandA.subtract(operandB);
                break;
            case CalculatorConstants.OP_MULTIPLY:
                result = operandA.multiply(operandB);
                break;
            case CalculatorConstants.OP_DIVIDE:
                if (BigDecimal.ZERO.compareTo(operandB) == 0) {
                    throw new BusinessException("B0003", "除数为零");
                }
                result = operandA.divide(operandB, CalculatorConstants.DEFAULT_SCALE,
                        RoundingMode.HALF_UP);
                break;
            default:
                throw new BusinessException("B0004", "不支持的操作类型: " + op);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("calculate: {} {} {} = {}", op, operandA, operandB, result);
        }

        // 构建DO（模拟持久化记录）
        CalculatorDO calcDO = new CalculatorDO();
        calcDO.setOperation(op);
        calcDO.setOperandA(operandA);
        calcDO.setOperandB(operandB);
        calcDO.setResult(result);
        calcDO.setGmtCreate(LocalDateTime.now());
        calcDO.setGmtModified(LocalDateTime.now());

        logger.info("计算完成, operation={}, operandA={}, operandB={}, result={}",
                op, operandA, operandB, result);

        return CalculatorVO.from(calcDO);
    }
}