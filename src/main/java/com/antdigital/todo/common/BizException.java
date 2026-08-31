package com.antdigital.todo.common;

/**
 * 业务异常，用于承载可预知的业务错误（如参数校验失败、唯一性冲突等）。
 *
 * <p>对应 exception-logging.md §2.14：使用有业务含义的自定义异常。</p>
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;

    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
