package com.dtazziboot.tools.common.exception;

import com.dtazziboot.tools.common.api.ApiResponse;
import com.dtazziboot.tools.common.constant.ErrorCodeConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理：统一转换为包含码值/信息/提示的响应体。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BizException.class)
    public ResponseEntity<ApiResponse<Void>> handleBizException(BizException e) {
        logger.warn("业务异常, errorCode: {}, message: {}", e.getErrorCode(), e.getMessage());
        ApiResponse<Void> response = ApiResponse.error(e.getErrorCode(), e.getMessage(), e.getUserTip());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidException(MethodArgumentNotValidException e) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getField)
                .collect(Collectors.joining(", "));
        logger.warn("参数校验失败, fields: {}", detail);
        ApiResponse<Void> response = ApiResponse.error(ErrorCodeConsts.ERR_TEXT_EMPTY,
                "参数校验失败: " + detail, "请检查输入参数");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        logger.error("系统异常, message: {}", e.getMessage(), e);
        ApiResponse<Void> response = ApiResponse.error(ErrorCodeConsts.ERR_SYSTEM,
                "系统内部错误", "服务暂时不可用，请稍后重试");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}