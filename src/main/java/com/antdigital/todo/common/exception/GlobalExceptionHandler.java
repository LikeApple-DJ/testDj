package com.antdigital.todo.common.exception;

import com.antdigital.todo.common.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 统一捕获 Controller 层抛出的异常，转换为标准 API 响应。
 * </p>
 *
 * @author AiWork
 * @date 2026/08/31
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 系统异常错误码 */
    private static final String SYS_ERROR_CODE = "TODO_999";

    /** 系统异常提示 */
    private static final String SYS_ERROR_MSG = "系统异常，请稍后重试";

    /** 参数校验错误码 */
    private static final String PARAM_ERROR_CODE = "TODO_999";

    /** 参数校验提示 */
    private static final String PARAM_ERROR_MSG = "请求参数格式错误";

    /**
     * 处理业务异常
     *
     * @param e 业务异常
     * @return 标准错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        logger.warn("业务异常, code: {}, msg: {}", e.getCode(), e.getMessage());
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@Valid 触发）
     *
     * @param e 参数校验异常
     * @return 标准错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
        // 拼接所有字段校验错误信息
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        logger.warn("参数校验失败, detail: {}", detail);
        return ApiResponse.error(PARAM_ERROR_CODE, detail);
    }

    /**
     * 处理未知系统异常
     *
     * @param e 未知异常
     * @return 标准错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception e) {
        logger.error("系统异常, errorMessage: {}", e.getMessage(), e);
        return ApiResponse.error(SYS_ERROR_CODE, SYS_ERROR_MSG);
    }
}
