package com.antdigital.todo.common.exception;

import com.antdigital.todo.common.constant.TodoConstants;
import com.antdigital.todo.common.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
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
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        FieldError firstError = fieldErrors.isEmpty() ? null : fieldErrors.get(0);
        String errorCode = resolveValidationErrorCode(firstError);
        // 拼接所有字段校验错误信息
        String detail = fieldErrors.stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        logger.warn("参数校验失败, code: {}, detail: {}", errorCode, detail);
        return ApiResponse.error(errorCode, detail);
    }

    /**
     * 处理请求体不可读异常（非JSON或格式错误）
     *
     * @param e 请求体不可读异常
     * @return 标准错误响应
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logger.warn("请求体格式错误, errorMessage: {}", e.getMessage());
        return ApiResponse.error(TodoConstants.CODE_SYSTEM_ERROR, TodoConstants.MSG_REQUEST_BODY_INVALID);
    }

    /**
     * 根据字段校验错误解析对应的业务错误码
     * <p>
     * 通过字段名与校验消息匹配 spec 定义的业务错误码：
     * title 空 → TODO_001、title 超长 → TODO_002、description 超长 → TODO_003
     * </p>
     *
     * @param fieldError 字段校验错误
     * @return 业务错误码
     */
    private String resolveValidationErrorCode(FieldError fieldError) {
        if (fieldError == null) {
            return TodoConstants.CODE_SYSTEM_ERROR;
        }
        String field = fieldError.getField();
        String message = fieldError.getDefaultMessage();
        if ("title".equals(field) && TodoConstants.MSG_TITLE_EMPTY.equals(message)) {
            return TodoConstants.CODE_TITLE_EMPTY;
        }
        if ("title".equals(field) && TodoConstants.MSG_TITLE_TOO_LONG.equals(message)) {
            return TodoConstants.CODE_TITLE_TOO_LONG;
        }
        if ("description".equals(field) && TodoConstants.MSG_DESCRIPTION_TOO_LONG.equals(message)) {
            return TodoConstants.CODE_DESCRIPTION_TOO_LONG;
        }
        return TodoConstants.CODE_SYSTEM_ERROR;
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
