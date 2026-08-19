package com.org.module.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.org.module.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
