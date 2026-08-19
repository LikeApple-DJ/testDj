package com.algorithm.demo.common;

/**
 * 统一响应结果封装
 *
 * @param <T> 数据类型
 * @author DTCoder
 */
public class Result<T> {

    /** 结果码 */
    private String code;

    /** 提示信息 */
    private String msg;

    /** 响应数据 */
    private T data;

    public Result() {
    }

    public Result(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功响应
     */
    public static <T> Result<T> success(T data) {
        return new Result<>("200", "SUCCESS", data);
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>("200", "SUCCESS", null);
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> fail(String code, String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * 参数错误响应
     */
    public static <T> Result<T> badRequest(String msg) {
        return new Result<>("400", msg, null);
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
}
