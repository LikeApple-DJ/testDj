package com.aiwork.todo.common.constant;

/**
 * 待办事项模块常量
 *
 * @author AiWork
 * @date 2026/09/01
 */
public final class TodoConstants {

    /** 事项名称最大长度 */
    public static final int MAX_TITLE_LENGTH = 100;

    /** 事项描述最大长度 */
    public static final int MAX_DESCRIPTION_LENGTH = 1000;

    /** 默认租户标识（本期上下文未带入时空串） */
    public static final String DEFAULT_TENANT_ID = "";

    /** 默认事项描述（描述选填为空时存空串） */
    public static final String DEFAULT_DESCRIPTION = "";

    /** 默认创建人标识（本期未实现登录时空串） */
    public static final String DEFAULT_CREATOR = "";

    /** 成功结果 code */
    public static final String SUCCESS_RESULT = "OK";

    /** 成功提示信息 */
    public static final String SUCCESS_MSG = "SUCCESS";

    /** 请求参数解析错误 code（框架层） */
    public static final String PARAM_ERROR_CODE = "PARAM_ERROR";

    /** 请求参数解析错误提示信息 */
    public static final String PARAM_ERROR_MSG = "请求格式错误";

    /** 功能开关关闭时的提示信息 */
    public static final String CREATE_DISABLED_MSG = "新增待办事项功能已关闭";

    private TodoConstants() {
        throw new IllegalStateException("Utility class");
    }
}
