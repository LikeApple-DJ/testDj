package com.antdigital.todo.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，统一异常到 ApiResponse 结构。
 *
 * <p>对应 design.md §5.1.3.1 异常场景表：
 * 参数反序列化/校验失败 → 框架统一异常处理返回对应错误码。</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常。
     *
     * @param ex 业务异常
     * @return 错误响应
     */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<ApiResponse<Void>> handleBizException(BizException ex) {
        logger.warn("业务异常, errorCode: {}, message: {}", ex.getErrorCode().getCode(), ex.getMessage());
        return ResponseEntity.ok(ApiResponse.fail(ex.getErrorCode(), ex.getMessage()));
    }

    /**
     * 处理参数校验异常（@Valid 触发）。
     *
     * <p>映射到 TODO_001（名称为空）/ TODO_002（名称超长）/ TODO_003（描述超长）。</p>
     *
     * @param ex 校验异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        ErrorCode errorCode = ErrorCode.TODO_900;
        if (fieldError != null) {
            String field = fieldError.getField();
            errorCode = switch (field) {
                case "name" -> fieldError.getCode() != null && fieldError.getCode().contains("Size")
                        ? ErrorCode.TODO_002 : ErrorCode.TODO_001;
                case "description" -> ErrorCode.TODO_003;
                default -> ErrorCode.TODO_900;
            };
        }
        logger.warn("参数校验失败, errorCode: {}, field: {}, message: {}",
                errorCode.getCode(),
                fieldError != null ? fieldError.getField() : "unknown",
                fieldError != null ? fieldError.getDefaultMessage() : "");
        return ResponseEntity.ok(ApiResponse.fail(errorCode));
    }

    /**
     * 处理唯一索引冲突（并发同名校验穿透）。
     *
     * <p>对应 design.md §5.1.3.1 并发控制：捕获 DuplicateKeyException，返回 TODO_004。</p>
     *
     * @param ex 唯一键冲突异常
     * @return 错误响应
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateKeyException(DuplicateKeyException ex) {
        logger.warn("唯一索引冲突, 返回 TODO_004: {}", ex.getMessage());
        return ResponseEntity.ok(ApiResponse.fail(ErrorCode.TODO_004));
    }

    /**
     * 处理未捕获的系统异常。
     *
     * <p>对应 design.md §5.1.3.1：数据库连接失败/写库异常返回 TODO_900，记录错误日志。</p>
     *
     * @param ex 系统异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleSystemException(Exception ex) {
        logger.error("系统异常, errorMessage: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(ErrorCode.TODO_900));
    }
}
