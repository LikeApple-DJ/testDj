package com.example.todo.exception;

/**
 * 待办事项模块错误码。
 *
 * <p>对应 design.md §5.2.2 错误码定义，格式 {@code TODO_{SEQ}}。</p>
 */
public enum TodoErrorCode {

    /** name 参数缺失或为空 */
    NAME_EMPTY("TODO_0001", "事项名称不能为空"),

    /** name 长度超过 200 字符 */
    NAME_TOO_LONG("TODO_0002", "事项名称长度超限"),

    /** description 长度超过 2000 字符 */
    DESCRIPTION_TOO_LONG("TODO_0003", "描述长度超限"),

    /** 系统内部错误（落库失败） */
    SYSTEM_ERROR("TODO_0004", "创建失败，请稍后重试"),

    /** 服务维护中（特性开关关闭） */
    SERVICE_DISABLED("TODO_0099", "服务维护中，请稍后再试");

    private final String code;

    private final String message;

    TodoErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
