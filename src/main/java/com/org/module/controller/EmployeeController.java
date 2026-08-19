package com.org.module.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.org.module.Result;
import com.org.module.dto.EmployeeDTO;
import com.org.module.dto.ResignDTO;
import com.org.module.dto.TransferDTO;
import com.org.module.entity.Employee;
import com.org.module.service.EmployeeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/check")
    public Result<Map<String, Boolean>> check(@RequestParam String field, @RequestParam String value) {
        if (field == null || field.isBlank() || value == null || value.isBlank()) {
            return Result.fail(400, "field 和 value 不能为空");
        }
        boolean exists = employeeService.checkFieldExists(field, value);
        return Result.ok(Map.of("isExist", exists));
    }

    @PostMapping
    public Result<Void> create(@RequestBody @Valid EmployeeDTO dto) {
        employeeService.createEmployee(dto);
        return Result.ok();
    }

    @GetMapping
    public Result<Page<Employee>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Integer status) {
        if (page < 1) {
            page = 1;
        }
        if (size < 1) {
            size = 10;
        } else if (size > 100) {
            size = 100;
        }
        Page<Employee> result = employeeService.lambdaQuery()
                .eq(deptId != null, Employee::getDeptId, deptId)
                .eq(status != null, Employee::getStatus, status)
                .eq(Employee::getIsDeleted, 0)
                .page(new Page<>(page, size));
        return Result.ok(result);
    }

    @PostMapping("/{id}/transfer")
    public Result<Void> transfer(@PathVariable Long id, @RequestBody @Valid TransferDTO dto) {
        try {
            employeeService.transferEmployee(id, dto);
        } catch (com.org.module.exception.BusinessException e) {
            log.warn("员工调动失败: {}", e.getMessage(), e);
            if ("ORG_409".equals(e.getCode())) {
                return Result.fail(409, e.getMessage());
            }
            throw e;
        }
        return Result.ok("调动成功");
    }

    @PutMapping("/{id}/resign")
    public Result<Void> resign(@PathVariable Long id, @RequestBody @Valid ResignDTO dto) {
        employeeService.resignEmployee(id, dto);
        return Result.ok();
    }
}
