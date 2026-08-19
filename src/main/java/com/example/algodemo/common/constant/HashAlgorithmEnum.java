package com.example.algodemo.common.constant;

import com.example.algodemo.common.exception.AlgorithmErrorCode;
import com.example.algodemo.common.exception.BusinessException;

import java.util.Locale;

/**
 * 支持的哈希算法枚举。
 *
 * <p>注意：{@link #MD5} 属于弱哈希算法，仅用于教学演示，生产环境请优先使用 SHA-256。</p>
 */
public enum HashAlgorithmEnum {

    /**
     * MD5 摘要算法（弱哈希，仅用于演示）。
     */
    MD5("MD5"),

    /**
     * SHA-256 摘要算法（推荐）。
     */
    SHA256("SHA-256");

    private final String digestName;

    HashAlgorithmEnum(String digestName) {
        this.digestName = digestName;
    }

    public String getDigestName() {
        return digestName;
    }

    /**
     * 根据算法名获取枚举，不区分大小写。
     *
     * @param name 算法名
     * @return 枚举值
     */
    public static HashAlgorithmEnum of(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException(AlgorithmErrorCode.ALG_002);
        }
        String normalized = name.trim().toUpperCase(Locale.ROOT);
        for (HashAlgorithmEnum value : values()) {
            if (value.name().equals(normalized)) {
                return value;
            }
        }
        throw new BusinessException(AlgorithmErrorCode.ALG_002);
    }
}
