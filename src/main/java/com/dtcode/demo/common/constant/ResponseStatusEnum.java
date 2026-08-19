package com.dtcode.demo.common.constant;

/**
 * 调用响应状态枚举
 *
 * @author DTCoder
 */
public enum ResponseStatusEnum {

    SUCCESS("SUCCESS", "调用成功"),
    FAIL("FAIL", "调用失败");

    private final String code;
    private final String description;

    ResponseStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
