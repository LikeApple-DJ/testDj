package com.example.todo.exception;

/**
 * 待办事项业务异常。
 *
 * <p>携带 {@link TodoErrorCode}，由全局异常处理器统一转换为对外响应。</p>
 */
public class TodoException extends RuntimeException {

    private final TodoErrorCode errorCode;

    /**
     * 构造业务异常。
     *
     * @param errorCode 错误码枚举
     */
    public TodoException(TodoErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 构造业务异常并携带原因。
     *
     * @param errorCode 错误码枚举
     * @param cause     原始异常
     */
    public TodoException(TodoErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public TodoErrorCode getErrorCode() {
        return errorCode;
    }
}
