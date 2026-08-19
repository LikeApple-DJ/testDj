package com.dtcode.demo.common.model;

import java.io.Serializable;

/**
 * 统一API响应对象
 *
 * @param <T> 业务数据类型
 * @author DTCoder
 */
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;
    private String msg;
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("OK", "SUCCESS", data);
    }

    public static <T> ApiResponse<T> error(String code, String msg) {
        return new ApiResponse<>(code, msg, null);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    @Override
    public String toString() {
        return "ApiResponse{" + "code='" + code + '\'' + ", msg='" + msg + '\'' + ", data=" + data + '}';
    }
}
