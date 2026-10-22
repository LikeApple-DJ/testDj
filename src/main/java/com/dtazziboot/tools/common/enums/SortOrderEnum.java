package com.dtazziboot.tools.common.enums;

import java.util.Arrays;

/**
 * 排序方向枚举。
 */
public enum SortOrderEnum {

    ASC,
    DESC;

    /**
     * 按名称解析，无法识别时返回 null。
     */
    public static SortOrderEnum fromName(String name) {
        if (name == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElse(null);
    }
}