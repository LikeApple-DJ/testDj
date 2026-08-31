package com.antdigital.todo.common.exception;

import lombok.Getter;

/**
 * 业务异常
 * <p>
 * 用于表示业务逻辑校验失败等可预期异常，携带错误码和提示信息。
 * </p>
 *
 * @author AiWork
 * @date 2026/08/31
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 错误码 */
    private final String code;

    /**
     * 构造业务异常
     *
     * @param code 错误码
     * @param msg  提示信息
     */
    public BusinessException(String code, String msg) {
        super(msg);
        this.code = code;
    }

    /**
     * 构造业务异常
     *
     * @param code 错误码
     * @param msg  提示信息
     * @param cause 原始异常
     */
    public BusinessException(String code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
    }
}
