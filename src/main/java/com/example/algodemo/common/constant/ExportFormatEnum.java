package com.example.algodemo.common.constant;

import com.example.algodemo.common.exception.AlgorithmErrorCode;
import com.example.algodemo.common.exception.BusinessException;

import java.util.Locale;

/**
 * 支持的导出格式枚举。
 */
public enum ExportFormatEnum {

    CSV,
    JSON;

    public static ExportFormatEnum of(String name) {
        if (name == null || name.trim().isEmpty()) {
            return CSV;
        }
        String normalized = name.trim().toUpperCase(Locale.ROOT);
        for (ExportFormatEnum value : values()) {
            if (value.name().equals(normalized)) {
                return value;
            }
        }
        throw new BusinessException(AlgorithmErrorCode.ALG_004);
    }
}
