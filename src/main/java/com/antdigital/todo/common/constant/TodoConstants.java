package com.antdigital.todo.common.constant;

/**
 * 待办事项模块常量
 * <p>
 * 集中管理魔法值，禁止在代码中直接出现未定义的常量。
 * </p>
 *
 * @author AiWork
 * @date 2026/08/31
 */
public final class TodoConstants {

    private TodoConstants() {
        throw new IllegalStateException("Utility class");
    }

    // ==================== 错误码 ====================

    /** 事项名称为空 */
    public static final String CODE_TITLE_EMPTY = "TODO_001";

    /** 事项名称超过100字符 */
    public static final String CODE_TITLE_TOO_LONG = "TODO_002";

    /** 事项描述超过500字符 */
    public static final String CODE_DESCRIPTION_TOO_LONG = "TODO_003";

    /** 系统异常 */
    public static final String CODE_SYSTEM_ERROR = "TODO_999";

    // ==================== 错误提示 ====================

    /** 事项名称不能为空 */
    public static final String MSG_TITLE_EMPTY = "事项名称不能为空";

    /** 事项名称长度需在1~100字符 */
    public static final String MSG_TITLE_TOO_LONG = "事项名称长度需在1~100字符";

    /** 事项描述长度不超过500字符 */
    public static final String MSG_DESCRIPTION_TOO_LONG = "事项描述长度不超过500字符";

    /** 系统异常，请稍后重试 */
    public static final String MSG_SYSTEM_ERROR = "系统异常，请稍后重试";

    /** 无法获取操作人信息 */
    public static final String MSG_CREATOR_MISSING = "无法获取操作人信息";

    /** 请求体格式错误 */
    public static final String MSG_REQUEST_BODY_INVALID = "请求体格式错误";

    // ==================== 字段长度限制 ====================

    /** 事项名称最大长度 */
    public static final int TITLE_MAX_LENGTH = 100;

    /** 事项描述最大长度 */
    public static final int DESCRIPTION_MAX_LENGTH = 500;

    /** 创建人标识最大长度 */
    public static final int CREATOR_MAX_LENGTH = 64;
}
