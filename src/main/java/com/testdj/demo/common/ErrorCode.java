package com.testdj.demo.common;

/**
 * 业务错误码定义。
 *
 * <p>错误码格式：MODULE_XXX，前三位表示模块，后三位表示具体错误。
 * <ul>
 *   <li>HASH_xxx：哈希模块</li>
 *   <li>SORT_xxx：排序模块</li>
 *   <li>EXPORT_xxx：导出模块</li>
 *   <li>METRICS_xxx：埋点报表模块</li>
 *   <li>SYSTEM_xxx：系统通用</li>
 * </ul>
 */
public final class ErrorCode {

    private ErrorCode() {
    }

    public static final int HASH_CONTENT_EMPTY = 100_001;
    public static final String HASH_CONTENT_EMPTY_MSG = "content must not be empty";

    public static final int HASH_UNSUPPORTED_ALGORITHM = 100_002;
    public static final String HASH_UNSUPPORTED_ALGORITHM_MSG = "unsupported algorithm";

    public static final int SORT_NUMBERS_EMPTY = 200_001;
    public static final String SORT_NUMBERS_EMPTY_MSG = "numbers must not be empty";

    public static final int EXPORT_UNKNOWN_TAB = 300_001;
    public static final String EXPORT_UNKNOWN_TAB_MSG = "unknown tab";

    public static final int EXPORT_UNSUPPORTED_FORMAT = 300_002;
    public static final String EXPORT_UNSUPPORTED_FORMAT_MSG = "unsupported format";

    public static final int EXPORT_GENERATE_EXCEL_FAILED = 300_003;
    public static final String EXPORT_GENERATE_EXCEL_FAILED_MSG = "failed to generate excel";

    public static final int METRICS_INVALID_DATE_RANGE = 400_001;
    public static final String METRICS_INVALID_DATE_RANGE_MSG = "startDate must not be after endDate";

    public static final int METRICS_INVALID_DIMENSION = 400_002;
    public static final String METRICS_INVALID_DIMENSION_MSG = "invalid dimension";

    public static final int SYSTEM_INTERNAL_ERROR = 900_001;
    public static final String SYSTEM_INTERNAL_ERROR_MSG = "internal server error";
}
