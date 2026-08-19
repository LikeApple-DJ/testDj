package com.org.module.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.org.module.dto.DepartmentTreeDTO;
import com.org.module.entity.Department;
import com.org.module.mapper.DepartmentMapper;
import com.org.module.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department>
        implements DepartmentService {

    @Override
    public List<DepartmentTreeDTO> getDepartmentTree() {
        List<Department> all = list();
        return buildTree(all, null);
    }

    private List<DepartmentTreeDTO> buildTree(List<Department> all, Long parentId) {
        return all.stream()
                .filter(d -> (parentId == null && d.getParentId() == null)
                        || (parentId != null && parentId.equals(d.getParentId())))
                .map(d -> {
                    DepartmentTreeDTO dto = new DepartmentTreeDTO();
                    dto.setId(d.getId());
                    dto.setName(d.getName());
                    dto.setChildren(buildTree(all, d.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void moveDepartment(Long id, Long newParentId) {
        if (newParentId != null && isDescendant(newParentId, id)) {
            throw new com.org.module.exception.BusinessException("不能将部门移动到自己的子孙节点下");
        }
        Department dept = getById(id);
        if (dept == null) {
            throw new com.org.module.exception.BusinessException("部门不存在");
        }
        dept.setParentId(newParentId);
        String parentPath = newParentId == null ? "" : getById(newParentId).getPath();
        dept.setPath(parentPath + "-" + id);
        updateById(dept);
    }

    private boolean isDescendant(Long ancestorId, Long targetId) {
        List<Department> children = lambdaQuery().eq(Department::getParentId, ancestorId).list();
        for (Department child : children) {
            if (child.getId().equals(targetId) || isDescendant(child.getId(), targetId)) {
                return true;
            }
        }
        return false;
    }
}
