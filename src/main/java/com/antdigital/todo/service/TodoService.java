package com.antdigital.todo.service;

import com.antdigital.todo.model.dto.TodoCreateRequest;
import com.antdigital.todo.model.dto.TodoCreateResponse;

/**
 * 待办事项业务服务接口。
 *
 * <p>对应 design.md §4.3 S01：boolean createTodo(TodoCreateRequest request)。</p>
 * <p>为便于统一出参包装，调整为返回 TodoCreateResponse（含新建ID）。</p>
 */
public interface TodoService {

    /**
     * 创建待办事项。
     *
     * <p>业务规则（design.md §5.1.3.1）：
     * R01 name 非空、长度1-128；
     * R02 description 长度0-1024；
     * R03 同租户未删除范围内 name 唯一；
     * R04 必须登录态，creator 取登录用户；
     * R05 status 默认0、is_deleted 默认0。</p>
     *
     * @param request 新增待办事项请求
     * @return 新建待办事项响应（含ID）
     */
    TodoCreateResponse createTodo(TodoCreateRequest request);
}
