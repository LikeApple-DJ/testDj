package com.dtcode.demo.common.exception;

import com.dtcode.demo.common.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author DTCoder
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleBusinessException(BusinessException e) {
        if (logger.isWarnEnabled()) {
            logger.warn("业务异常: errorCode={}, message={}", e.getErrorCode(), e.getMessage());
        }
        return ApiResponse.error(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception e) {
        logger.error("系统异常: {}", e.getMessage(), e);
        return ApiResponse.error("B0001", "系统内部错误");
    }
}
