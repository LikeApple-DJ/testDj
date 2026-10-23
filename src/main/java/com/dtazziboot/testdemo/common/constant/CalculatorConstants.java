package com.dtazziboot.testdemo.common.constant;

/**
 * 计算器业务常量
 */
public final class CalculatorConstants {

    private CalculatorConstants() {
        throw new IllegalStateException("Constant class");
    }

    /** 精度：保留小数位数 */
    public static final int DEFAULT_SCALE = 4;

    /** 操作类型 */
    public static final String OP_ADD = "ADD";
    public static final String OP_SUBTRACT = "SUBTRACT";
    public static final String OP_MULTIPLY = "MULTIPLY";
    public static final String OP_DIVIDE = "DIVIDE";
}