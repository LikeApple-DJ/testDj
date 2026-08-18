package com.example.demo.common;

/**
 * 统一 API 响应包装类。
 *
 * @param <T> 业务数据类型
 */
public class ApiResponse<T> {

    private String code;
    private String msg;
    private T data;

    private ApiResponse() {
    }

    /**
     * 成功响应。
     *
     * @param data 业务数据
     * @param <T>  数据类型
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.code = "200";
        response.msg = "SUCCESS";
        response.data = data;
        return response;
    }

    /**
     * 错误响应。
     *
     * @param code 错误码
     * @param msg  错误信息
     * @param <T>  数据类型
     * @return ApiResponse
     */
    public static <T> ApiResponse<T> error(String code, String msg) {
        ApiResponse<T> response = new ApiResponse<>();
        response.code = code;
        response.msg = msg;
        response.data = null;
        return response;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}