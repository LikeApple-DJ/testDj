package com.org.module.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.org.module.dto.DepartmentTreeDTO;
import com.org.module.entity.Department;
import com.org.module.mapper.DepartmentMapper;
import com.org.module.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(rollbackFor = Exception.class)
    public void moveDepartment(Long id, Long newParentId) {
        if (id == null) {
            throw new com.org.module.exception.BusinessException("部门ID不能为空");
        }
        
        // 防循环引用：newParentId 不能是 id 自身或其子孙节点
        if (newParentId != null) {
            if (newParentId.equals(id)) {
                throw new com.org.module.exception.BusinessException("不能将部门移动到自己下面");
            }
            if (isDescendant(id, newParentId)) {
                throw new com.org.module.exception.BusinessException("不能将部门移动到自己的子孙节点下");
            }
        }

        Department dept = getById(id);
        if (dept == null) {
            throw new com.org.module.exception.BusinessException("部门不存在");
        }

        // 计算新 path
        String newPath;
        if (newParentId == null) {
            newPath = String.valueOf(id);
        } else {
            Department parent = getById(newParentId);
            if (parent == null) {
                throw new com.org.module.exception.BusinessException("目标父部门不存在");
            }
            newPath = parent.getPath() + "-" + id;
        }

        dept.setParentId(newParentId);
        dept.setPath(newPath);
        updateById(dept);

        // 级联更新所有子孙部门的 path
        updateDescendantPaths(id, newPath);
    }

    /**
     * 检查 targetId 是否是 ancestorId 的子孙节点（包括直接子节点和间接后代）
     */
    private boolean isDescendant(Long ancestorId, Long targetId) {
        List<Department> children = lambdaQuery().eq(Department::getParentId, ancestorId).list();
        for (Department child : children) {
            if (child.getId().equals(targetId) || isDescendant(child.getId(), targetId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 级联更新子孙部门的 path
     */
    private void updateDescendantPaths(Long parentId, String parentPath) {
        List<Department> children = lambdaQuery().eq(Department::getParentId, parentId).list();
        for (Department child : children) {
            String childPath = parentPath + "-" + child.getId();
            child.setPath(childPath);
            updateById(child);
            updateDescendantPaths(child.getId(), childPath);
        }
    }
}
