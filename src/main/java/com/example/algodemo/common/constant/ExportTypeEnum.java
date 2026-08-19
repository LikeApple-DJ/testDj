package com.example.algodemo.common.constant;

import com.example.algodemo.common.exception.AlgorithmErrorCode;
import com.example.algodemo.common.exception.BusinessException;

import java.util.Locale;

/**
 * 支持的导出类型枚举。
 */
public enum ExportTypeEnum {

    HELLO("hello"),
    HASH("hash"),
    BUBBLE_SORT("bubbleSort");

    private final String code;

    ExportTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ExportTypeEnum of(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new BusinessException(AlgorithmErrorCode.ALG_001);
        }
        String normalized = type.trim().toLowerCase(Locale.ROOT);
        for (ExportTypeEnum value : values()) {
            if (value.getCode().equals(normalized)) {
                return value;
            }
        }
        throw new BusinessException(AlgorithmErrorCode.ALG_001);
    }
}
