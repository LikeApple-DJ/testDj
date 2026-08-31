package com.antdigital.todo.enums;

/**
 * 逻辑删除标记枚举。
 *
 * <p>对应 design.md §5.1.1.2：0-未删除 / 1-已删除。</p>
 */
public enum IsDeleted {

    /** 未删除 */
    NOT_DELETED(0, "未删除"),

    /** 已删除 */
    DELETED(1, "已删除");

    private final int code;
    private final String description;

    IsDeleted(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
