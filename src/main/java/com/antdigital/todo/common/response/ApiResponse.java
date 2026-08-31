package com.antdigital.todo.common.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一 API 响应体
 * <p>
 * 前后端交互的响应结构，包含 code（结果码）、msg（提示信息）、data（业务数据）。
 * code 为 "OK" 表示成功，其他值为错误码。
 * </p>
 *
 * @author AiWork
 * @date 2026/08/31
 */
@Data
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 成功结果码 */
    public static final String SUCCESS_CODE = "OK";

    /** 成功提示 */
    public static final String SUCCESS_MSG = "SUCCESS";

    /** 结果码，OK 表示成功 */
    private String code;

    /** 提示信息 */
    private String msg;

    /** 业务数据 */
    private T data;

    /**
     * 构造成功响应
     *
     * @param data 业务数据
     * @param <T>  数据类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(SUCCESS_CODE);
        response.setMsg(SUCCESS_MSG);
        response.setData(data);
        return response;
    }

    /**
     * 构造失败响应
     *
     * @param code 错误码
     * @param msg  提示信息
     * @param <T>  数据类型
     * @return 失败响应
     */
    public static <T> ApiResponse<T> error(String code, String msg) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMsg(msg);
        response.setData(null);
        return response;
    }
}
