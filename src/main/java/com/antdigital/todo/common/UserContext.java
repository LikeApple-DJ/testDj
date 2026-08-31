package com.antdigital.todo.common;

/**
 * 当前登录用户上下文（ThreadLocal 持有）。
 *
 * <p>对应 design.md §6.4.2.3：全局统一拦截器校验登录态后，
 * 将 tenant_id 与 creator 注入上下文，禁止客户端传入。</p>
 */
public class UserContext {

    private static final ThreadLocal<UserInfo> HOLDER = new ThreadLocal<>();

    /**
     * 设置当前线程的登录用户信息。
     *
     * @param tenantId 租户ID
     * @param creator  创建人（登录态用户标识）
     */
    public static void set(String tenantId, String creator) {
        HOLDER.set(new UserInfo(tenantId, creator));
    }

    /**
     * 获取当前线程的登录用户信息。
     *
     * @return 用户信息，未登录时返回 null
     */
    public static UserInfo get() {
        return HOLDER.get();
    }

    /**
     * 清除当前线程的登录用户信息。
     */
    public static void clear() {
        HOLDER.remove();
    }

    /**
     * 获取当前租户ID。
     *
     * @return 租户ID
     */
    public static String getTenantId() {
        UserInfo info = HOLDER.get();
        return info == null ? null : info.tenantId();
    }

    /**
     * 获取当前创建人。
     *
     * @return 创建人标识
     */
    public static String getCreator() {
        UserInfo info = HOLDER.get();
        return info == null ? null : info.creator();
    }

    /**
     * 登录用户信息记录。
     *
     * @param tenantId 租户ID
     * @param creator  创建人
     */
    public record UserInfo(String tenantId, String creator) {
    }
}
