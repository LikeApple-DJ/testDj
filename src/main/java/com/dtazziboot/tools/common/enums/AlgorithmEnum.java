package com.dtazziboot.tools.common.enums;

import java.util.Arrays;

/**
 * 哈希算法枚举。
 */
public enum AlgorithmEnum {

    MD5("MD5"),
    SHA256("SHA-256");

    private final String jcaName;

    AlgorithmEnum(String jcaName) {
        this.jcaName = jcaName;
    }

    public String getJcaName() {
        return jcaName;
    }

    /**
     * 按名称解析，无法识别时返回 null（调用方转参数错误码），避免 valueOf 抛 IllegalArgumentException。
     */
    public static AlgorithmEnum fromName(String name) {
        if (name == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElse(null);
    }
}