package com.aiwork.todo.common.exception;

/**
 * 业务异常，承载模块错误码与提示信息
 *
 * <p>对 http/api 开放接口使用错误码方式返回，应用内部推荐异常抛出。</p>
 *
 * @author AiWork
 * @date 2026/09/01
 */
public class BizException extends RuntimeException {

    /** 错误码 */
    private final String errorCode;

    /**
     * 基于错误码枚举构造业务异常
     *
     * @param errorCode 错误码枚举
     */
    public BizException(TodoErrorCodeEnum errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
    }

    /**
     * 基于错误码与提示信息构造业务异常
     *
     * @param errorCode 错误码
     * @param message   提示信息
     */
    public BizException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public String getErrorCode() {
        return errorCode;
    }
}
