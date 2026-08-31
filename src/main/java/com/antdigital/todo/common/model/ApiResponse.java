package com.antdigital.todo.common.model;

/**
 * 通用响应包装类，统一出参结构：{result, msg, data}
 *
 * @param <T> data 载荷类型
 * @author AiWork
 * @date 2026/08/31
 */
public class ApiResponse<T> {

    /** 结果 code，成功为 "OK"，失败为错误码 */
    private String result;

    /** 提示信息，成功为 "SUCCESS" */
    private String msg;

    /** 数据载荷 */
    private T data;

    public ApiResponse() {
    }

    /**
     * 构造成功响应
     *
     * @param data 数据载荷
     * @param <T>  载荷类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.result = "OK";
        response.msg = "SUCCESS";
        response.data = data;
        return response;
    }

    /**
     * 构造失败响应
     *
     * @param errorCode 错误码
     * @param message   提示信息
     * @param <T>       载荷类型
     * @return 失败响应
     */
    public static <T> ApiResponse<T> error(String errorCode, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.result = errorCode;
        response.msg = message;
        response.data = null;
        return response;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
