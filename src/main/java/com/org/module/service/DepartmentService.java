package com.org.module.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.org.module.dto.DepartmentTreeDTO;
import com.org.module.entity.Department;
import java.util.List;

public interface DepartmentService extends IService<Department> {
    List<DepartmentTreeDTO> getDepartmentTree();
    void moveDepartment(Long id, Long newParentId);
}
