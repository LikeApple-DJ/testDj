package com.antdigital.todo.todo.repository;

import com.antdigital.todo.todo.model.entity.TodoDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 待办事项数据访问层
 *
 * @author AiWork
 * @date 2026/08/31
 */
@Repository
public interface TodoRepository extends JpaRepository<TodoDO, Long> {
}
