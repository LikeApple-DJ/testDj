package com.dt.example.common;

/**
 * 统一响应结构。
 *
 * @param code    状态码，0 表示成功
 * @param message 提示信息
 * @param data    业务数据，失败时为 null
 * @param <T>     业务数据类型
 */
public record Result<T>(int code, String message, T data) {

    public static <T> Result<T> ok(T data) {
        return new Result<>(0, "ok", data);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }
}