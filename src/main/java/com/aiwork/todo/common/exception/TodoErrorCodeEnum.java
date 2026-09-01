package com.aiwork.todo.common.exception;

/**
 * 待办事项模块错误码枚举
 *
 * <p>错误码格式：{MODULE}_{SEQ}，模块编码 TODO，如 TODO_001。</p>
 *
 * @author AiWork
 * @date 2026/09/01
 */
public enum TodoErrorCodeEnum {

    /** 事项名称不能为空 */
    TODO_001("TODO_001", "事项名称不能为空"),

    /** 事项名称超过 100 字符 */
    TODO_002("TODO_002", "事项名称超过 100 字符"),

    /** 事项描述超过 1000 字符 */
    TODO_003("TODO_003", "事项描述超过 1000 字符"),

    /** 系统异常 */
    TODO_999("TODO_999", "系统异常");

    /** 错误码 */
    private final String code;

    /** 提示信息 */
    private final String message;

    TodoErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取提示信息
     *
     * @return 提示信息
     */
    public String getMessage() {
        return message;
    }
}
