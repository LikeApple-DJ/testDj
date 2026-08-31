package com.antdigital.todo.common.exception;

import com.antdigital.todo.common.model.ApiResponse;
import com.antdigital.todo.todo.enums.TodoErrorCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器，统一将异常转换为 ApiResponse 响应
 *
 * @author AiWork
 * @date 2026/08/31
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     *
     * @param ex 业务异常
     * @return 错误响应
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleBusinessException(BusinessException ex) {
        logger.warn("业务异常, errorCode: {}, errorMessage: {}", ex.getErrorCode(), ex.getErrorMessage());
        return ApiResponse.error(ex.getErrorCode(), ex.getErrorMessage());
    }

    /**
     * 处理参数校验异常（@Valid 不通过）
     *
     * @param ex 参数校验异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException ex) {
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));
        logger.warn("参数校验失败: {}", detail);
        return ApiResponse.error(TodoErrorCodeEnum.SYSTEM_ERROR.getErrorCode(), "请求参数格式错误");
    }

    /**
     * 兜底处理未知异常
     *
     * @param ex 未知异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleUnknownException(Exception ex) {
        logger.error("系统内部错误, errorMessage: {}", ex.getMessage(), ex);
        return ApiResponse.error(TodoErrorCodeEnum.SYSTEM_ERROR.getErrorCode(), "系统繁忙，请稍后重试");
    }
}
