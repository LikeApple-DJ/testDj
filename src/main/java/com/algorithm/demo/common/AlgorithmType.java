package com.algorithm.demo.common;

/**
 * 算法类型枚举
 *
 * @author DTCoder
 */
public enum AlgorithmType {

    /** HelloWorld 算法 */
    HELLO("HelloWorld"),

    /** 哈希算法 */
    HASH("哈希算法"),

    /** 冒泡排序 */
    SORT("冒泡排序");

    private final String description;

    AlgorithmType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据名称安全获取枚举值
     *
     * @param name 枚举名称
     * @return 枚举值
     * @throws IllegalArgumentException 名称不合法时抛出
     */
    public static AlgorithmType fromName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("算法类型不能为空");
        }
        try {
            return AlgorithmType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("不支持的算法类型: " + name);
        }
    }
}
