package com.org.module.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.org.module.dto.TodoCreateRequest;
import com.org.module.entity.TodoItem;

/**
 * 待办事项业务服务。
 */
public interface TodoService extends IService<TodoItem> {

    /**
     * 新增待办事项。
     *
     * @param request 新增请求（事项名称、描述）
     * @param creator 创建人标识，来源请求头 X-User-Id，缺失时传 null
     * @return 新建待办事项主键
     */
    Long createTodo(TodoCreateRequest request, String creator);
}
