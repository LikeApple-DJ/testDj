package com.example.demo.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，统一返回 {@link ApiResult} 格式的错误响应。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理 @Valid 校验失败。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Void>> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        log.warn("参数校验失败: {}", msg);
        return ResponseEntity.badRequest()
                .body(ApiResult.error(400, msg));
    }

    /**
     * 处理请求体解析失败（如 JSON 格式错误）。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResult<Void>> handleNotReadable(HttpMessageNotReadableException e) {
        log.warn("请求体解析失败", e);
        return ResponseEntity.badRequest()
                .body(ApiResult.error(400, "请求体格式错误"));
    }

    /**
     * 处理参数非法。
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResult<Void>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("参数非法: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResult.error(400, e.getMessage()));
    }

    /**
     * 兜底处理未预期异常。
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Void>> handleGeneral(Exception e) {
        log.error("未预期异常", e);
        return ResponseEntity.internalServerError()
                .body(ApiResult.error(500, "服务器内部错误"));
    }
}