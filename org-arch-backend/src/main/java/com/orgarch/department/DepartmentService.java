package com.orgarch.department;

import com.orgarch.common.BizException;
import com.orgarch.employee.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    private final DepartmentRepository deptRepo;
    private final EmployeeRepository empRepo;

    public DepartmentService(DepartmentRepository deptRepo,
                             EmployeeRepository empRepo) {
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
                .orElseThrow(() -> new BizException(400, "部门不存在: " + parentId));
        return deptRepo.findByParentIdAndIsDeletedOrderBySortOrderAscIdAsc(parentId, 0)
                .stream()
                .map(d -> new DepartmentTreeVo(d.getId(), d.getName(), d.getParentId()))
                .collect(Collectors.toList());
    }

    /**
     * 拖拽层级变更（F04/F12）：含循环引用校验与 path 重算，全程事务。
     */
    @Transactional
    public void move(Long id, Long newParentId) {
        // 行级悲观锁：串行化对同一节点的拖拽，保障子树 path 重算原子性（design 5.1.3.2 方案C 降级）
        Department target = deptRepo.findByIdForUpdate(id)
                .filter(d -> d.getIsDeleted() == 0)
                .orElseThrow(() -> new BizException(400, "部门不存在: " + id));

        // R01: newParentId 为 0 或 null 表示移动到根级
        boolean toRoot = newParentId == null || newParentId == 0L;

        // R02: newParentId 不能等于被移动节点自身
        if (!toRoot && newParentId.equals(id)) {
            throw new BizException(400, "不能将部门移动到自身之下");
        }

        String newParentPath;
        Long newParentIdResolved;
        if (toRoot) {
            newParentPath = "/";
            newParentIdResolved = null;
        } else {
            Department parent = deptRepo.findById(newParentId)
                    .filter(d -> d.getIsDeleted() == 0)
                    .orElseThrow(() -> new BizException(400, "目标父部门不存在: " + newParentId));
            // R03: newParentId 不能是被移动节点的子孙（循环引用防护）
            if (isAncestorOrSelf(target, parent)) {
                throw new BizException(400, "不能将部门移动到其自身或子孙部门之下");
            }
            newParentPath = parent.getPath();
            newParentIdResolved = parent.getId();
        }

        String oldPath = target.getPath();
        String newPath = newParentPath + target.getId() + "/";
        target.setParentId(newParentIdResolved);
        target.setPath(newPath);
        deptRepo.save(target);

        // R04: 级联重算所有子孙 path 前缀
        List<Department> descendants = deptRepo.findByPathPrefix(oldPath)
                .stream()
                .filter(d -> !d.getId().equals(target.getId()))
                .toList();
        for (Department d : descendants) {
            d.setPath(newPath + d.getPath().substring(oldPath.length()));
            deptRepo.save(d);
        }
    }

    /**
     * parent 是否是 target 的祖先或自身（基于 path 前缀，R03）。
     * path 以 '/' 分隔且以 '/' 结尾，startsWith 可正确判定祖先关系，无多位数歧义。
     */
    private boolean isAncestorOrSelf(Department target, Department parent) {
        String targetPath = target.getPath(); // 例如 /1/2/
        String parentPath = parent.getPath(); // 例如 /1/2/3/
        return parentPath.startsWith(targetPath);
    }

    /**
     * 部门删除校验（F13/F16）：部门下有员工或子部门禁止删除，逻辑删除。
     */
    @Transactional
    public void delete(Long id) {
        Department d = deptRepo.findById(id)
                .filter(x -> x.getIsDeleted() == 0)
                .orElseThrow(() -> new BizException(400, "部门不存在: " + id));
        long empCount = empRepo.countByDeptIdAndIsDeleted(id, 0);
        if (empCount > 0) {
            throw new BizException(400, "该部门下存在" + empCount + "名员工，请先转移人员后再删除");
        }
        if (deptRepo.countByParentIdAndIsDeleted(id, 0) > 0) {
            throw new BizException(400, "该部门下存在子部门，请先处理子部门后再删除");
        }
        d.setIsDeleted(1);
        deptRepo.save(d);
    }
}
