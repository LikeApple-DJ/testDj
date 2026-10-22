package com.dtazziboot.tools.common.enums;

import java.util.Arrays;

/**
 * 导出类型枚举：对应前端不同 Tab 的展示结果。
 */
public enum ExportTypeEnum {

    HELLOWORLD("helloworld"),
    HASH("hash"),
    BUBBLE_SORT("bubble-sort"),
    REPORT("report");

    private final String code;

    ExportTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ExportTypeEnum fromCode(String code) {
        if (code == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(e -> e.code.equalsIgnoreCase(code.trim()))
                .findFirst()
                .orElse(null);
    }
}