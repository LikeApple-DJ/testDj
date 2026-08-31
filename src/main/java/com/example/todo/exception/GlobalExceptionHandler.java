package com.example.todo.exception;

import com.example.todo.dto.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器，统一输出 {@link ApiResponse} 结构。
 *
 * <p>负责将参数校验异常映射为精确错误码（TODO_0001/0002/0003），
 * 将系统异常映射为 TODO_0004，并记录日志。</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理 @RequestBody 参数校验失败。
     *
     * @param ex 校验异常
     * @return 通用失败响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        TodoErrorCode code = resolveFieldErrorCode(ex);
        logger.warn("参数校验失败: {}, code: {}", ex.getMessage(), code.getCode());
        return ResponseEntity.ok(ApiResponse.fail(code.getCode(), code.getMessage()));
    }

    /**
     * 处理 @RequestParam/@PathVariable 校验失败。
     *
     * @param ex 校验异常
     * @return 通用失败响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(ConstraintViolationException ex) {
        String detail = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        logger.warn("约束校验失败: {}", detail);
        return ResponseEntity.ok(ApiResponse.fail(TodoErrorCode.NAME_EMPTY.getCode(), detail));
    }

    /**
     * 处理参数类型不匹配。
     *
     * @param ex 类型不匹配异常
     * @return 通用失败响应
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        logger.warn("参数类型不匹配: {}", ex.getMessage());
        return ResponseEntity.ok(ApiResponse.fail(TodoErrorCode.SYSTEM_ERROR.getCode(), "参数类型不匹配"));
    }

    /**
     * 处理业务异常。
     *
     * @param ex 业务异常
     * @return 通用失败响应
     */
    @ExceptionHandler(TodoException.class)
    public ResponseEntity<ApiResponse<Object>> handleBiz(TodoException ex) {
        logger.warn("业务异常: code={}, msg={}", ex.getErrorCode().getCode(), ex.getMessage());
        return ResponseEntity.ok(ApiResponse.fail(ex.getErrorCode().getCode(), ex.getMessage()));
    }

    /**
     * 兜底系统异常处理。
     *
     * @param ex 系统异常
     * @return 通用失败响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleSystem(Exception ex) {
        logger.error("系统内部错误, msg: {}", ex.getMessage(), ex);
        return ResponseEntity.ok(ApiResponse.fail(TodoErrorCode.SYSTEM_ERROR.getCode(),
                TodoErrorCode.SYSTEM_ERROR.getMessage()));
    }

    /**
     * 根据首个字段错误映射到精确错误码。
     *
     * @param ex 校验异常
     * @return 错误码
     */
    private TodoErrorCode resolveFieldErrorCode(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        if (fieldError == null) {
            return TodoErrorCode.SYSTEM_ERROR;
        }
        String field = fieldError.getField();
        String code = fieldError.getCode();
        if ("name".equals(field)) {
            if ("NotBlank".equals(code) || "NotNull".equals(code)) {
                return TodoErrorCode.NAME_EMPTY;
            }
            if ("Size".equals(code)) {
                return TodoErrorCode.NAME_TOO_LONG;
            }
        }
        if ("description".equals(field) && "Size".equals(code)) {
            return TodoErrorCode.DESCRIPTION_TOO_LONG;
        }
        return TodoErrorCode.SYSTEM_ERROR;
    }
}
