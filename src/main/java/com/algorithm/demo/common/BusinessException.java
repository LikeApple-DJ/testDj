package com.algorithm.demo.common;

/**
 * 业务异常类
 *
 * @author DTCoder
 */
public class BusinessException extends RuntimeException {

    /** 错误码 */
    private final String errorCode;

    /** 错误信息 */
    private final String errorMessage;

    public BusinessException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BusinessException(String errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
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
