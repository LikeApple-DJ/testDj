package com.dtazziboot.tools.common.util;

/**
 * 登录态用户上下文。
 *
 * <p>埋点采集时从 ThreadLocal 读取调用人及人员维度；未登录时降级为 anonymous，
 * 避免因登录态缺失影响主链路。</p>
 */
public final class UserContext {

    private static final ThreadLocal<String> INVOKER_ID = new ThreadLocal<>();

    private static final ThreadLocal<String> INVOKER_NAME = new ThreadLocal<>();

    private static final ThreadLocal<String> PERSON_TYPE = new ThreadLocal<>();

    private static final ThreadLocal<String> PERSON_LEVEL = new ThreadLocal<>();

    private static final ThreadLocal<String> PERSON_DEPT = new ThreadLocal<>();

    private UserContext() {
        throw new IllegalStateException("Utility class");
    }

    public static String getInvokerId() {
        return firstNonNull(INVOKER_ID.get(), "anonymous");
    }

    public static String getInvokerName() {
        return firstNonNull(INVOKER_NAME.get(), "anonymous");
    }

    public static String getPersonType() {
        return firstNonNull(PERSON_TYPE.get(), "unknown");
    }

    public static String getPersonLevel() {
        return firstNonNull(PERSON_LEVEL.get(), "unknown");
    }

    public static String getPersonDept() {
        return firstNonNull(PERSON_DEPT.get(), "unknown");
    }

    public static void set(String invokerId, String invokerName, String personType,
                           String personLevel, String personDept) {
        INVOKER_ID.set(invokerId);
        INVOKER_NAME.set(invokerName);
        PERSON_TYPE.set(personType);
        PERSON_LEVEL.set(personLevel);
        PERSON_DEPT.set(personDept);
    }

    public static void clear() {
        INVOKER_ID.remove();
        INVOKER_NAME.remove();
        PERSON_TYPE.remove();
        PERSON_LEVEL.remove();
        PERSON_DEPT.remove();
    }

    private static String firstNonNull(String value, String fallback) {
        return value == null || value.isEmpty() ? fallback : value;
    }
}