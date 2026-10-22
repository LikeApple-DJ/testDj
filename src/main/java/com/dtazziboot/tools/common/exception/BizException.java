package com.dtazziboot.tools.common.exception;

/**
 * 业务异常：携带错误码、排查信息与用户提示三要素。
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String errorCode;

    private final String userTip;

    public BizException(String errorCode, String errorMessage, String userTip) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.userTip = userTip;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getUserTip() {
        return userTip;
    }
}