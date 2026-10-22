package com.dtazziboot.tools.common.constant;

/**
 * 错误码常量。
 *
 * <p>格式：来源位（A=用户端 / B=系统 / C=第三方）+ 四位数字，共 5 位。</p>
 */
public final class ErrorCodeConsts {

    private ErrorCodeConsts() {
        throw new IllegalStateException("Utility class");
    }

    /** 哈希算法非法。 */
    public static final String ERR_ALGORITHM = "A0001";

    /** 待哈希文本为空。 */
    public static final String ERR_TEXT_EMPTY = "A0002";

    /** 排序方向非法。 */
    public static final String ERR_ORDER = "A0003";

    /** 排序数组长度超限。 */
    public static final String ERR_NUMS_TOO_LONG = "A0004";

    /** 导出类型非法。 */
    public static final String ERR_EXPORT_TYPE = "A0005";

    /** 报表维度非法。 */
    public static final String ERR_DIMENSION = "A0006";

    /** 系统内部错误。 */
    public static final String ERR_SYSTEM = "B0001";
}