package com.orgarch.department;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    private final DepartmentRepository deptRepo;
    private final com.orgarch.employee.EmployeeRepository empRepo;

    public DepartmentService(DepartmentRepository deptRepo,
                             com.orgarch.employee.EmployeeRepository empRepo) {
        this.deptRepo = deptRepo;
        this.empRepo = empRepo;
    }

    @Transactional(readOnly = true)
    public List<DepartmentTreeVo> buildTree() {
        List<Department> all = deptRepo.findAllActive();
        Map<Long, DepartmentTreeVo> nodeMap = new HashMap<>();
        List<DepartmentTreeVo> roots = new ArrayList<>();
        for (Department d : all) {
            nodeMap.put(d.getId(), new DepartmentTreeVo(d.getId(), d.getName(), d.getParentId()));
        }
        for (Department d : all) {
            DepartmentTreeVo node = nodeMap.get(d.getId());
            if (d.getParentId() == null || !nodeMap.containsKey(d.getParentId())) {
                roots.add(node);
            } else {
                nodeMap.get(d.getParentId()).getChildren().add(node);
            }
        }
        return roots;
    }

    @Transactional(readOnly = true)
    public List<DepartmentTreeVo> findChildren(Long parentId) {
        deptRepo.findById(parentId)
                .filter(d -> d.getIsDeleted() == 0)
                .orElseThrow(() -> new com.orgarch.common.BizException(400, "部门不存在: " + parentId));
        return deptRepo.findByParentIdAndIsDeletedOrderBySortOrderAscIdAsc(parentId, 0)
                .stream()
                .map(d -> new DepartmentTreeVo(d.getId(), d.getName(), d.getParentId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void move(Long id, Long newParentId) {
        Department target = deptRepo.findById(id)
                .filter(d -> d.getIsDeleted() == 0)
                .orElseThrow(() -> new com.orgarch.common.BizException(400, "部门不存在: " + id));

        if (newParentId != null && newParentId.equals(id)) {
            throw new com.orgarch.common.BizException(400, "不能将部门移动到自身之下");
        }

        String newParentPath;
        Long newParentIdResolved;
        if (newParentId == null) {
            newParentPath = "/";
            newParentIdResolved = null;
        } else {
            Department parent = deptRepo.findById(newParentId)
                    .filter(d -> d.getIsDeleted() == 0)
                    .orElseThrow(() -> new com.orgarch.common.BizException(400, "目标父部门不存在: " + newParentId));
            if (isAncestorOrSelf(target, parent)) {
                throw new com.orgarch.common.BizException(400, "不能将部门移动到其自身或子孙部门之下");
            }
            newParentPath = parent.getPath();
            newParentIdResolved = parent.getId();
        }

        String oldPath = target.getPath();
        String newPath = newParentPath + target.getId() + "/";
        target.setParentId(newParentIdResolved);
        target.setPath(newPath);
        deptRepo.save(target);

        // 级联更新子孙 path 前缀
        List<Department> descendants = deptRepo.findByPathPrefix(oldPath)
                .stream()
                .filter(d -> !d.getId().equals(target.getId()))
                .toList();
        for (Department d : descendants) {
            d.setPath(newPath + d.getPath().substring(oldPath.length()));
            deptRepo.save(d);
        }
    }

    /** parent 是否是 target 的祖先或自身（基于 path 前缀）。 */
    private boolean isAncestorOrSelf(Department target, Department parent) {
        String targetPath = target.getPath(); // 例如 /1/2/
        String parentPath = parent.getPath(); // 例如 /1/2/3/
        return parentPath.startsWith(targetPath);
    }

    @Transactional
    public void delete(Long id) {
        Department d = deptRepo.findById(id)
                .filter(x -> x.getIsDeleted() == 0)
                .orElseThrow(() -> new com.orgarch.common.BizException(400, "部门不存在: " + id));
        long empCount = empRepo.countByDeptIdAndIsDeleted(id, 0);
        if (empCount > 0) {
            throw new com.orgarch.common.BizException(400, "该部门下存在" + empCount + "名员工，请先转移人员后再删除");
        }
        if (deptRepo.countByParentIdAndIsDeleted(id, 0) > 0) {
            throw new com.orgarch.common.BizException(400, "该部门下存在子部门，请先处理子部门后再删除");
        }
        d.setIsDeleted(1);
        deptRepo.save(d);
    }
}
