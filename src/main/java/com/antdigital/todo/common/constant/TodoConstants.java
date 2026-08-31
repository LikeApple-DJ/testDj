package com.antdigital.todo.common.constant;

/**
 * 待办模块常量
 *
 * @author AiWork
 * @date 2026/08/31
 */
public final class TodoConstants {

    /** 默认租户标识 */
    public static final String DEFAULT_TENANT_ID = "default";

    /** 事项名称最大长度 */
    public static final int MAX_NAME_LENGTH = 100;

    /** 事项描述最大长度 */
    public static final int MAX_DESCRIPTION_LENGTH = 500;

    private TodoConstants() {
        throw new IllegalStateException("Utility class");
    }
}
