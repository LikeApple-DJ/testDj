package com.orgarch.department;

import com.orgarch.common.BizException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DepartmentMoveTest {

    @Autowired private DepartmentRepository deptRepo;
    @Autowired private DepartmentService deptService;

    private Long createDept(String name, Long parentId, String path) {
        Department d = new Department(name, parentId);
        d.setPath(path);
        return deptRepo.save(d).getId();
    }

    @Test
    void move_updatesPathAndDescendants() {
        Long rootId = createDept("研发部", null, "/" + 1 + "/");
        rootId = deptRepo.save(deptRepo.findById(rootId).orElseThrow()).getId();
        Department root = deptRepo.findById(rootId).orElseThrow();
        root.setPath("/" + root.getId() + "/");
        deptRepo.save(root);

        Long childId = createDept("前端组", root.getId(), root.getPath() + "X/");
        Department child = deptRepo.findById(childId).orElseThrow();
        child.setPath(root.getPath() + child.getId() + "/");
        deptRepo.save(child);

        Long otherRootId = createDept("研发二部", null, "/Z/");
        Department otherRoot = deptRepo.findById(otherRootId).orElseThrow();
        otherRoot.setPath("/" + otherRoot.getId() + "/");
        deptRepo.save(otherRoot);

        deptService.move(child.getId(), otherRoot.getId());

        Department moved = deptRepo.findById(child.getId()).orElseThrow();
        assertEquals(otherRoot.getPath() + child.getId() + "/", moved.getPath());
        assertEquals(otherRoot.getId(), moved.getParentId());
    }

    @Test
    void move_toOwnDescendant_isRejected() {
        Long rootId = createDept("研发部", null, "/1/");
        Department root = deptRepo.findById(rootId).orElseThrow();
        root.setPath("/" + root.getId() + "/");
        deptRepo.save(root);
        // 创建子部门，path 先用占位再校正为父路径 + 自身 ID
        Long childId = createDept("前端组", root.getId(), root.getPath() + "tmp/");
        Department child = deptRepo.findById(childId).orElseThrow();
        child.setPath(root.getPath() + child.getId() + "/");
        deptRepo.save(child);

        // 试图把父节点移动到其子孙之下 → 必须被拒绝（避免死循环）
        BizException ex = assertThrows(BizException.class,
                () -> deptService.move(root.getId(), child.getId()));
        assertEquals(400, ex.getCode());
    }

    @Test
    void move_toSelf_isRejected() {
        Long rootId = createDept("研发部", null, "/1/");
        // 目标父节点就是自身 → 必须被拒绝
        BizException ex = assertThrows(BizException.class,
                () -> deptService.move(rootId, rootId));
        assertEquals(400, ex.getCode());
    }
}
