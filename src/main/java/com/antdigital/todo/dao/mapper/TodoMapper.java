package com.antdigital.todo.dao.mapper;

import com.antdigital.todo.model.entity.TodoDO;
import org.apache.ibatis.annotations.Param;

/**
 * 待办事项数据访问接口。
 *
 * <p>对应 design.md §5.1.3.1 时序图中的 Mapper 层。</p>
 */
public interface TodoMapper {

    /**
     * 查询同租户下未删除的同名记录（唯一性预校验，R03）。
     *
     * @param tenantId 租户ID
     * @param name     事项名称
     * @return 已存在的记录（可能为 null）
     */
    TodoDO selectByTenantAndName(@Param("tenantId") String tenantId, @Param("name") String name);

    /**
     * 插入待办事项记录，回写自增主键。
     *
     * @param todo 待办事项数据对象
     * @return 受影响行数
     */
    int insert(TodoDO todo);
}
