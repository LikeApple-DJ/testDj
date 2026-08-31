package com.example.todo.dto;

/**
 * 通用响应包装结构：{ result, msg, data }。
 *
 * <p>对应 design.md §5.1 通用出参约定。</p>
 *
 * @param <T> data 负载类型
 */
public class ApiResponse<T> {

    /** 结果 code：成功为 "OK"，失败为错误码 */
    private String result;

    /** 提示信息 */
    private String msg;

    /** 业务数据负载 */
    private T data;

    public ApiResponse() {
    }

    private ApiResponse(String result, String msg, T data) {
        this.result = result;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 构建成功响应。
     *
     * @param data 业务数据
     * @param <T>  负载类型
     * @return 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("OK", "SUCCESS", data);
    }

    /**
     * 构建失败响应。
     *
     * @param errorCode 错误码
     * @param msg       提示信息
     * @param <T>       负载类型
     * @return 失败响应
     */
    public static <T> ApiResponse<T> fail(String errorCode, String msg) {
        return new ApiResponse<>(errorCode, msg, null);
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

    @Override
    public String toString() {
        return "ApiResponse{"
                + "result='" + result + '\''
                + ", msg='" + msg + '\''
                + ", data=" + data
                + '}';
    }
}
