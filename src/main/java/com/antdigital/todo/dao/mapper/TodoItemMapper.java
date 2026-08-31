package com.antdigital.todo.dao.mapper;

import com.antdigital.todo.model.entity.TodoItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 待办事项数据访问接口
 *
 * @author AiWork
 * @date 2026/08/31
 */
@Mapper
public interface TodoItemMapper {

    /**
     * 插入待办事项记录，返回影响行数
     *
     * @param todoItem 待办事项数据对象
     * @return 影响行数
     */
    int insert(TodoItemDO todoItem);
}
