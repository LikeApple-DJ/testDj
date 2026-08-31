package com.example.todo.context;

/**
 * 当前登录用户上下文，提供 creatorId 来源。
 *
 * <p>对应 design.md A01：creator_id 取登录态用户ID。
 * 具体登录态校验由全局拦截器负责（本期假设），此处仅提供读取入口。</p>
 */
public interface UserContext {

    /**
     * 获取当前登录用户ID。
     *
     * @return 用户ID，未登录或无法识别时返回 null
     */
    Long getCurrentUserId();
}
