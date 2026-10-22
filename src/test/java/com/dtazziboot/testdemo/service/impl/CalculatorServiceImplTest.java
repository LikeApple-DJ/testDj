package com.dtazziboot.testdemo.service.impl;

import com.dtazziboot.testdemo.common.constant.CalculatorConstants;
import com.dtazziboot.testdemo.common.exception.BusinessException;
import com.dtazziboot.testdemo.model.dto.CalculatorVO;
import com.dtazziboot.testdemo.service.CalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * CalculatorServiceImpl 单元测试
 *
 * @see CalculatorServiceImpl
 */
class CalculatorServiceImplTest {

    private CalculatorService calculatorService;

    @BeforeEach
    void setUp() {
        calculatorService = new CalculatorServiceImpl();
    }

    // ==================== calculate 加法测试 ====================

    @Test
    void should_returnCorrectSum_when_addTwoPositiveNumbers() {
        // Arrange
        BigDecimal operandA = new BigDecimal("10.5");
        BigDecimal operandB = new BigDecimal("20.3");

        // Act
        CalculatorVO result = calculatorService.calculate(
                CalculatorConstants.OP_ADD, operandA, operandB);

        // Assert
        assertThat(result.getOperation()).isEqualTo(CalculatorConstants.OP_ADD);
        assertThat(result.getResult()).isEqualTo("30.8");
    }

    @Test
    void should_returnCorrectSum_when_addNegativeNumbers() {
        // Arrange
        BigDecimal operandA = new BigDecimal("-5.0");
        BigDecimal operandB = new BigDecimal("-3.2");

        // Act
        CalculatorVO result = calculatorService.calculate(
                CalculatorConstants.OP_ADD, operandA, operandB);

        // Assert
        assertThat(result.getResult()).isEqualTo("-8.2");
    }

    // ==================== calculate 减法测试 ====================

    @Test
    void should_returnCorrectDifference_when_subtractTwoNumbers() {
        // Arrange
        BigDecimal operandA = new BigDecimal("100");
        BigDecimal operandB = new BigDecimal("33.33");

        // Act
        CalculatorVO result = calculatorService.calculate(
                CalculatorConstants.OP_SUBTRACT, operandA, operandB);

        // Assert
        assertThat(result.getOperation()).isEqualTo(CalculatorConstants.OP_SUBTRACT);
        assertThat(result.getResult()).isEqualTo("66.67");
    }

    // ==================== calculate 乘法测试 ====================

    @Test
    void should_returnCorrectProduct_when_multiplyTwoNumbers() {
        // Arrange
        BigDecimal operandA = new BigDecimal("7.5");
        BigDecimal operandB = new BigDecimal("4");

        // Act
        CalculatorVO result = calculatorService.calculate(
                CalculatorConstants.OP_MULTIPLY, operandA, operandB);

        // Assert
        assertThat(result.getResult()).isEqualTo("30");
    }

    @Test
    void should_returnZero_when_multiplyByZero() {
        // Arrange
        BigDecimal operandA = new BigDecimal("999.99");
        BigDecimal operandB = BigDecimal.ZERO;

        // Act
        CalculatorVO result = calculatorService.calculate(
                CalculatorConstants.OP_MULTIPLY, operandA, operandB);

        // Assert
        assertThat(result.getResult()).isEqualTo("0");
    }

    // ==================== calculate 除法测试 ====================

    @Test
    void should_returnCorrectQuotient_when_divideTwoNumbers() {
        // Arrange
        BigDecimal operandA = new BigDecimal("10");
        BigDecimal operandB = new BigDecimal("3");

        // Act
        CalculatorVO result = calculatorService.calculate(
                CalculatorConstants.OP_DIVIDE, operandA, operandB);

        // Assert
        assertThat(result.getOperation()).isEqualTo(CalculatorConstants.OP_DIVIDE);
        assertThat(result.getResult()).isEqualTo("3.3333");
    }

    @Test
    void should_throwException_when_divideByZero() {
        // Arrange
        BigDecimal operandA = new BigDecimal("10");
        BigDecimal operandB = BigDecimal.ZERO;

        // Act & Assert
        assertThatThrownBy(() -> calculatorService.calculate(
                CalculatorConstants.OP_DIVIDE, operandA, operandB))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("除数为零");
    }

    // ==================== 参数校验测试 ====================

    @Test
    void should_throwException_when_operationIsNull() {
        // Arrange
        BigDecimal operandA = new BigDecimal("1");
        BigDecimal operandB = new BigDecimal("2");

        // Act & Assert
        assertThatThrownBy(() -> calculatorService.calculate(null, operandA, operandB))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("操作类型不能为空");
    }

    @Test
    void should_throwException_when_operationIsBlank() {
        // Arrange
        BigDecimal operandA = new BigDecimal("1");
        BigDecimal operandB = new BigDecimal("2");

        // Act & Assert
        assertThatThrownBy(() -> calculatorService.calculate(" ", operandA, operandB))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("操作类型不能为空");
    }

    @Test
    void should_throwException_when_operationIsInvalid() {
        // Arrange
        BigDecimal operandA = new BigDecimal("1");
        BigDecimal operandB = new BigDecimal("2");

        // Act & Assert
        assertThatThrownBy(() -> calculatorService.calculate("MOD", operandA, operandB))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("不支持的操作类型");
    }

    @Test
    void should_throwException_when_operandAIsNull() {
        // Arrange
        BigDecimal operandB = new BigDecimal("2");

        // Act & Assert
        assertThatThrownBy(() -> calculatorService.calculate(
                CalculatorConstants.OP_ADD, null, operandB))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("操作数不能为空");
    }

    @Test
    void should_throwException_when_operandBIsNull() {
        // Arrange
        BigDecimal operandA = new BigDecimal("2");

        // Act & Assert
        assertThatThrownBy(() -> calculatorService.calculate(
                CalculatorConstants.OP_ADD, operandA, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("操作数不能为空");
    }

    // ==================== 边界值测试 ====================

    @Test
    void should_handleLargeNumbers_when_addMaxValues() {
        // Arrange
        BigDecimal operandA = new BigDecimal("9999999999999999.9999");
        BigDecimal operandB = new BigDecimal("0.0001");

        // Act
        CalculatorVO result = calculatorService.calculate(
                CalculatorConstants.OP_ADD, operandA, operandB);

        // Assert
        assertThat(result.getResult()).isEqualTo("10000000000000000");
    }
}