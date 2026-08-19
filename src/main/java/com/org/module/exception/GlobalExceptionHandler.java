package com.org.module.exception;

import com.org.module.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e) {
        return Result.fail(400, e.getMessage());
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public Result<Void> handleOptimisticLock(OptimisticLockingFailureException e) {
        return Result.fail(409, "该记录已被他人修改，请刷新重试");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.fail(500, "系统异常");
    }
}
