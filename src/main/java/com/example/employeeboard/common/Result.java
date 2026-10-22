package com.example.employeeboard.common;

/**
 * 通用出参结构 {code, msg, data}，成功 code="OK"。
 *
 * @param <T> 数据类型
 */
public class Result<T> {

    public static final String CODE_OK = "OK";

    private String code;
    private String msg;
    private T data;

    public Result() {
    }

    public Result(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(CODE_OK, "SUCCESS", data);
    }

    public static <T> Result<T> fail(String code, String msg) {
        return new Result<>(code, msg, null);
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
