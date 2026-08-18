package com.example.demo.common.exception;

/**
 * Demo 模块业务异常。
 */
public class DemoException extends RuntimeException {

    private final String errorCode;

    /**
     * 构造方法。
     *
     * @param errorCode 错误码
     * @param message   错误信息
     */
    public DemoException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}