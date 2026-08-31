package com.antdigital.todo.todo.enums;

/**
 * 待办模块错误码枚举
 *
 * @author AiWork
 * @date 2026/08/31
 */
public enum TodoErrorCodeEnum {

    /** name 缺失或为空白 */
    NAME_BLANK("TODO_0001", "事项名称不可为空"),

    /** name 长度超过 100 字符 */
    NAME_TOO_LONG("TODO_0002", "事项名称过长"),

    /** description 长度超过 500 字符 */
    DESCRIPTION_TOO_LONG("TODO_0003", "事项描述过长"),

    /** 系统内部错误 */
    SYSTEM_ERROR("TODO_0004", "系统繁忙，请稍后重试");

    /** 错误码 */
    private final String errorCode;

    /** 用户可读提示信息 */
    private final String errorMessage;

    TodoErrorCodeEnum(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
