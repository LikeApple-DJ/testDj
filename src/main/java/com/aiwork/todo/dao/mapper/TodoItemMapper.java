package com.aiwork.todo.dao.mapper;

import com.aiwork.todo.model.entity.TodoItemDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 待办事项数据访问映射
 *
 * @author AiWork
 * @date 2026/09/01
 */
@Mapper
public interface TodoItemMapper {

    /**
     * 新增待办事项记录，主键回写至入参对象 id 字段
     *
     * @param todoItem 待办事项数据对象
     * @return 受影响行数
     */
    int insert(TodoItemDO todoItem);
}
