package com.example.cost.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.cost.entity.Department;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DepartmentMapper extends BaseMapper<Department> {}