package com.antdigital.todo.enums;

/**
 * 待办事项状态枚举。
 *
 * <p>对应 design.md §5.1.1.2：0-待处理 / 1-进行中 / 2-已完成。</p>
 */
public enum TodoStatus {

    /** 待处理 */
    PENDING(0, "待处理"),

    /** 进行中 */
    IN_PROGRESS(1, "进行中"),

    /** 已完成 */
    COMPLETED(2, "已完成");

    private final int code;
    private final String description;

    TodoStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据 code 获取枚举值。
     *
     * @param code 状态编码
     * @return 对应枚举值，无匹配时返回 null
     */
    public static TodoStatus getByCode(int code) {
        for (TodoStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
