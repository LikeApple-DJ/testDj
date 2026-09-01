package com.aiwork.todo.common.exception;

import com.aiwork.todo.common.constant.TodoConstants;
import com.aiwork.todo.common.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 *
 * <p>最外层业务使用者必须将异常转化为用户可理解的内容，不向调用方抛栈。</p>
 *
 * @author AiWork
 * @date 2026/09/01
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常，按错误码返回
     *
     * @param e 业务异常
     * @return 错误响应
     */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBizException(BizException e) {
        logger.warn("biz error, errorCode: {}, message: {}", e.getErrorCode(), e.getMessage());
        return Result.error(e.getErrorCode(), e.getMessage());
    }

    /**
     * 处理请求体解析异常（入参 JSON 格式错误）
     *
     * @param e 解析异常
     * @return 错误响应
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logger.warn("request body parse error: {}", e.getMessage());
        return Result.error(TodoConstants.PARAM_ERROR_CODE, TodoConstants.PARAM_ERROR_MSG);
    }

    /**
     * 兜底处理系统异常，返回 TODO_999 并记录堆栈
     *
     * @param e 系统异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        logger.error("system error", e);
        return Result.error(TodoErrorCodeEnum.TODO_999.getCode(), TodoErrorCodeEnum.TODO_999.getMessage());
    }
}
