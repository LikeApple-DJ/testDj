package com.antdigital.todo.common.exception;

/**
 * 业务异常，承载错误码与用户可读提示信息
 *
 * @author AiWork
 * @date 2026/08/31
 */
public class BusinessException extends RuntimeException {

    /** 错误码 */
    private final String errorCode;

    /** 用户可读提示信息 */
    private final String errorMessage;

    /**
     * 构造业务异常
     *
     * @param errorCode    错误码
     * @param errorMessage 用户可读提示信息
     */
    public BusinessException(String errorCode, String errorMessage) {
        super(errorMessage);
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
