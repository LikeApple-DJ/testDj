package com.testdj.demo.exception;

import com.testdj.demo.common.ApiResponse;
import com.testdj.demo.common.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException e) {
        LOGGER.warn("business exception: code={}, message={}", e.getCode(), e.getMessage(), e);
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e) {
        LOGGER.error("internal server error", e);
        return ApiResponse.error(ErrorCode.SYSTEM_INTERNAL_ERROR, ErrorCode.SYSTEM_INTERNAL_ERROR_MSG);
    }
}
