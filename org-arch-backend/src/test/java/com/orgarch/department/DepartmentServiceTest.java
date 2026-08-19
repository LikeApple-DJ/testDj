package com.orgarch.department;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DepartmentServiceTest {

    @Autowired private DepartmentRepository deptRepo;
    @Autowired private DepartmentService deptService;

    @Test
    void buildTree_assemblesRootAndChildren() {
        Department root = deptRepo.save(new Department("研发部", null));
        root.setPath("/" + root.getId() + "/");
        deptRepo.save(root);
        Department child = deptRepo.save(new Department("前端组", root.getId()));
        child.setPath(root.getPath() + child.getId() + "/");
        deptRepo.save(child);

        List<DepartmentTreeVo> tree = deptService.buildTree();

        assertEquals(1, tree.size());
        assertEquals("研发部", tree.get(0).getName());
        assertEquals(1, tree.get(0).getChildren().size());
        assertEquals("前端组", tree.get(0).getChildren().get(0).getName());
    }

    @Test
    void findChildren_lazyLoadsChildDepartments() {
        Department root = deptRepo.save(new Department("研发部", null));
        root.setPath("/" + root.getId() + "/");
        deptRepo.save(root);
        Department child = deptRepo.save(new Department("前端组", root.getId()));
        child.setPath(root.getPath() + child.getId() + "/");
        deptRepo.save(child);

        List<DepartmentTreeVo> children = deptService.findChildren(root.getId());

        assertEquals(1, children.size());
        assertEquals("前端组", children.get(0).getName());
    }
}
