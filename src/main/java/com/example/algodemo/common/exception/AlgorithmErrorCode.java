package com.example.algodemo.common.exception;

/**
 * 算法演示模块错误码。
 *
 * <p>错误码格式沿用系分设计：ALG_{SEQ}。</p>
 */
public enum AlgorithmErrorCode {

    OK("OK", "SUCCESS"),
    ALG_001("ALG_001", "参数非法"),
    ALG_002("ALG_002", "不支持的哈希算法"),
    ALG_003("ALG_003", "排序数组为空或格式错误"),
    ALG_004("ALG_004", "导出格式不支持"),
    ALG_005("ALG_005", "导出类型不支持"),
    ALG_999("ALG_999", "系统内部错误");

    private final String code;
    private final String message;

    AlgorithmErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
