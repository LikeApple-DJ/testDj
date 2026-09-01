package com.org.module.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.org.module.entity.Todo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 待办事项数据访问
 */
@Mapper
public interface TodoMapper extends BaseMapper<Todo> {
}
