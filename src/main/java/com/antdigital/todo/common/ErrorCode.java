package com.antdigital.todo.common;

/**
 * 错误码枚举，对应 design.md §5.1 全局约定。
 *
 * <p>格式：{MODULE}_{SEQ}，待办事项模块前缀 TODO_。</p>
 */
public enum ErrorCode {

    /** 成功 */
    OK("OK", "SUCCESS"),

    /** 事项名称为空（R01） */
    TODO_001("TODO_001", "事项名称为空"),

    /** 事项名称超过128字符（R01） */
    TODO_002("TODO_002", "事项名称超过128字符"),

    /** 事项描述超过1024字符（R02） */
    TODO_003("TODO_003", "事项描述超过1024字符"),

    /** 同租户下事项名称已存在（R03） */
    TODO_004("TODO_004", "同租户下事项名称已存在"),

    /** 登录态缺失（R04） */
    TODO_005("TODO_005", "登录态缺失"),

    /** 系统异常 */
    TODO_900("TODO_900", "系统异常");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
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
