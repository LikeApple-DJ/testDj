package com.aiwork.todo.common.result;

import com.aiwork.todo.common.constant.TodoConstants;

/**
 * 通用出参结构
 *
 * <p>结构：{ "result": "OK"/错误码, "msg": "提示信息", "data": {} }</p>
 *
 * @param <T> 业务数据类型
 * @author AiWork
 * @date 2026/09/01
 */
public class Result<T> {

    /** 结果 code，成功为 OK */
    private String result;

    /** 提示信息 */
    private String msg;

    /** 业务数据 */
    private T data;

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

    /**
     * 构建成功响应（带业务数据）
     *
     * @param data 业务数据
     * @param <T>  业务数据类型
     * @return 成功响应
     */
    public static <T> Result<T> success(T data) {
        Result<T> response = new Result<>();
        response.setResult(TodoConstants.SUCCESS_RESULT);
        response.setMsg(TodoConstants.SUCCESS_MSG);
        response.setData(data);
        return response;
    }

    /**
     * 构建错误响应
     *
     * @param errorCode 错误码
     * @param msg       提示信息
     * @param <T>       业务数据类型
     * @return 错误响应
     */
    public static <T> Result<T> error(String errorCode, String msg) {
        Result<T> response = new Result<>();
        response.setResult(errorCode);
        response.setMsg(msg);
        response.setData(null);
        return response;
    }
}
