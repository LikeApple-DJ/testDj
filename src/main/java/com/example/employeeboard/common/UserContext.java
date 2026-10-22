package com.example.employeeboard.common;

/**
 * 当前登录用户上下文（由 AuthInterceptor 写入，复用统一登录态）。
 */
public final class UserContext {

    private static final ThreadLocal<String> CURRENT_EMP_NO = new ThreadLocal<>();

    private UserContext() {
    }

    public static void setCurrentEmpNo(String empNo) {
        CURRENT_EMP_NO.set(empNo);
    }

    public static String getCurrentEmpNo() {
        return CURRENT_EMP_NO.get();
    }

    public static void clear() {
        CURRENT_EMP_NO.remove();
    }
}
