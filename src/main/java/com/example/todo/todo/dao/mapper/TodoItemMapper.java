package com.example.todo.todo.dao.mapper;

import com.example.todo.todo.dao.entity.TodoItemDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 待办事项 - 数据访问层
 */
@Mapper
public interface TodoItemMapper {

    /**
     * 新增待办事项
     *
     * @param todoItem 待办事项实体
     * @return 影响行数
     */
    int insert(TodoItemDO todoItem);

    /**
     * 根据ID查询待办事项
     *
     * @param id 主键ID
     * @return 待办事项实体
     */
    TodoItemDO selectById(@Param("id") Long id);
}