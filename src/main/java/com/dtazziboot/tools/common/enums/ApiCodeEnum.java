package com.dtazziboot.tools.common.enums;

/**
 * 接口编码枚举：埋点流水表 api_code 取值。
 */
public enum ApiCodeEnum {

    HELLO("HELLO", "helloworld"),
    HASH("HASH", "哈希算法"),
    BUBBLE_SORT("BUBBLE_SORT", "冒泡排序"),
    EXPORT("EXPORT", "导出"),
    REPORT_TREND("REPORT_TREND", "调用趋势"),
    REPORT_DISTRIBUTION("REPORT_DISTRIBUTION", "维度分布");

    private final String code;

    private final String name;

    ApiCodeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}