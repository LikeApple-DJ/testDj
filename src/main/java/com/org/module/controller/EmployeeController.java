package com.org.module.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.org.module.Result;
import com.org.module.dto.EmployeeDTO;
import com.org.module.dto.ResignDTO;
import com.org.module.dto.TransferDTO;
import com.org.module.entity.Employee;
import com.org.module.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/check")
    public Result<Map<String, Boolean>> check(@RequestParam String field, @RequestParam String value) {
        boolean exists = employeeService.checkFieldExists(field, value);
        return Result.ok(Map.of("isExist", exists));
    }

    @PostMapping
    public Result<Void> create(@RequestBody EmployeeDTO dto) {
        employeeService.createEmployee(dto);
        return Result.ok();
    }

    @GetMapping
    public Result<Page<Employee>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Integer status) {
        Page<Employee> result = employeeService.lambdaQuery()
                .eq(deptId != null, Employee::getDeptId, deptId)
                .eq(status != null, Employee::getStatus, status)
                .eq(Employee::getIsDeleted, 0)
                .page(new Page<>(page, size));
        return Result.ok(result);
    }

    @PostMapping("/{id}/transfer")
    public Result<Void> transfer(@PathVariable Long id, @RequestBody TransferDTO dto) {
        try {
            employeeService.transferEmployee(id, dto);
        } catch (com.org.module.exception.BusinessException e) {
            if (e.getMessage().contains("已被他人修改")) {
                return Result.fail(409, e.getMessage());
            }
            throw e;
        }
        return Result.ok("调动成功");
    }

    @PutMapping("/{id}/resign")
    public Result<Void> resign(@PathVariable Long id, @RequestBody ResignDTO dto) {
        employeeService.resignEmployee(id, dto);
        return Result.ok();
    }
}
