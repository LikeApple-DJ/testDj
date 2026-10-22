package com.dtazziboot.tools.common.api;

import java.io.Serializable;

/**
 * 统一响应体。
 *
 * <p>成功：code=200，data 携带业务数据；失败：errorCode/errorMessage/userTip 三要素齐全。</p>
 */
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** HTTP 语义状态码。 */
    private Integer code;

    /** 简短提示信息。 */
    private String message;

    /** 业务数据。 */
    private T data;

    /** 错误码（A/B/C + 四位数字）。 */
    private String errorCode;

    /** 面向排查人员的错误信息。 */
    private String errorMessage;

    /** 面向最终用户的提示信息。 */
    private String userTip;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> error(String errorCode, String errorMessage, String userTip) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(500);
        response.setMessage("error");
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        response.setUserTip(userTip);
        return response;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getUserTip() {
        return userTip;
    }

    public void setUserTip(String userTip) {
        this.userTip = userTip;
    }
}