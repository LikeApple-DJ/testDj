package com.dtazziboot.tools.common.enums;

import java.util.Arrays;

/**
 * 报表统计维度枚举。
 */
public enum ReportDimensionEnum {

    /** 人员类型。 */
    TYPE,
    /** 人员层级。 */
    LEVEL,
    /** 人员部门。 */
    DEPT;

    public static ReportDimensionEnum fromName(String name) {
        if (name == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElse(null);
    }
}