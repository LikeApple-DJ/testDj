package com.example.demo.common;

public class ApiResult<T> {
    private int code;
    private String message;
    private T data;

    private ApiResult() {}

    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> r = new ApiResult<>();
        r.code = 0;
        r.message = "success";
        r.data = data;
        return r;
    }

    public static <T> ApiResult<T> error(int code, String message) {
        ApiResult<T> r = new ApiResult<>();
        r.code = code;
        r.message = message;
        r.data = null;
        return r;
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}