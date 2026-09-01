package com.org.module.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.org.module.dto.TodoDTO;
import com.org.module.entity.Todo;

/**
 * 待办事项服务
 */
public interface TodoService extends IService<Todo> {

    /**
     * 新增待办事项
     *
     * @param dto 待办事项创建请求
     */
    void createTodo(TodoDTO dto);
}
