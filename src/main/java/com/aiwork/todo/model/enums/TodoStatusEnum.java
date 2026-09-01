package com.aiwork.todo.model.enums;

/**
 * 待办事项状态枚举
 *
 * <p>本期仅使用 PENDING；DONE/DELETED 为预留状态，避免后续扩展时改表。</p>
 *
 * @author AiWork
 * @date 2026/09/01
 */
public enum TodoStatusEnum {

    /** 待处理（本期默认） */
    PENDING("PENDING", "待处理"),

    /** 已完成（本期不使用，预留） */
    DONE("DONE", "已完成"),

    /** 已删除（本期不使用，预留） */
    DELETED("DELETED", "已删除");

    /** 状态编码 */
    private final String code;

    /** 状态描述 */
    private final String description;

    TodoStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 获取状态编码
     *
     * @return 状态编码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取状态描述
     *
     * @return 状态描述
     */
    public String getDescription() {
        return description;
    }
}
