package com.org.module.controller;

import com.org.module.Result;
import com.org.module.dto.DepartmentTreeDTO;
import com.org.module.entity.Employee;
import com.org.module.service.DepartmentService;
import com.org.module.service.EmployeeService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final EmployeeService employeeService;

    public DepartmentController(DepartmentService departmentService, EmployeeService employeeService) {
        this.departmentService = departmentService;
        this.employeeService = employeeService;
    }

    @GetMapping("/tree")
    public Result<List<DepartmentTreeDTO>> getTree() {
        return Result.ok(departmentService.getDepartmentTree());
    }

    @PutMapping("/{id}/move")
    public Result<Void> move(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Long newParentId = body.get("newParentId");
        departmentService.moveDepartment(id, newParentId);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        long count = employeeService.lambdaQuery()
                .eq(Employee::getDeptId, id)
                .eq(Employee::getIsDeleted, 0)
                .eq(Employee::getStatus, 1)
                .count();
        if (count > 0) {
            return Result.fail(400, "该部门下存在" + count + "名员工，请先转移人员后再删除");
        }
        departmentService.removeById(id);
        return Result.ok();
    }
}
