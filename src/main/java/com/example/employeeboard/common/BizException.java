package com.example.employeeboard.common;

/**
 * 业务异常，错误码格式 {MODULE}_{SEQ}，如 EMP_001。
 */
public class BizException extends RuntimeException {

    private final String errorCode;

    public BizException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
