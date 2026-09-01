package com.org.module.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.org.module.dto.TodoDTO;
import com.org.module.dto.TodoVO;
import com.org.module.entity.Todo;

/**
 * 待办事项业务服务
 */
public interface TodoService extends IService<Todo> {

    /**
     * 新增待办事项
     *
     * @param dto 待办事项信息
     * @return 新建事项展示对象
     */
    TodoVO createTodo(TodoDTO dto);
}
