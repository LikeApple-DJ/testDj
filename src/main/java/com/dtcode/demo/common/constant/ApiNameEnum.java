package com.dtcode.demo.common.constant;

/**
 * 接口名称枚举
 *
 * @author DTCoder
 */
public enum ApiNameEnum {

    HELLOWORLD("helloworld", "HelloWorld接口"),
    HASH("hash", "哈希算法接口"),
    BUBBLE_SORT("bubble-sort", "冒泡排序接口");

    private final String code;
    private final String description;

    ApiNameEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取枚举值
     *
     * @param code 枚举code
     * @return 枚举值，未匹配返回null
     */
    public static ApiNameEnum fromCode(String code) {
        for (ApiNameEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }
}
