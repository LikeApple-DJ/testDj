package com.example.employeeboard.employee;

import com.example.employeeboard.common.BizException;
import com.example.employeeboard.common.PageResult;
import com.example.employeeboard.common.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 员工管理端接口（W01-W06）。
 */
@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/create")
    public Result<EmployeeDTO> create(@Valid @RequestBody EmployeeCreateReq req) {
        return Result.ok(employeeService.create(req));
    }

    @PostMapping("/update")
    public Result<Boolean> update(@Valid @RequestBody EmployeeUpdateReq req) {
        return Result.ok(employeeService.update(req));
    }

    @PostMapping("/delete")
    public Result<Boolean> delete(@RequestBody Map<String, @NotBlank String> req) {
        return Result.ok(employeeService.deleteByEmpNo(req.get("empNo")));
    }

    @PostMapping("/pageQuery")
    public Result<PageResult<EmployeeDTO>> pageQuery(@Valid @RequestBody EmployeeQueryReq req) {
        // 设计约定：列表接口手机号默认中间四位打码（design.md §6.4.3.2）
        return Result.ok(employeeService.pageQuery(req, true));
    }

    @GetMapping("/detail")
    public Result<EmployeeDTO> detail(@RequestParam String empNo) {
        EmployeeDTO dto = employeeService.getByEmpNo(empNo);
        if (dto == null) {
            throw new BizException("EMP_003", "员工不存在");
        }
        return Result.ok(dto);
    }

    @GetMapping("/summary")
    public Result<EmployeeSummaryDTO> summary() {
        return Result.ok(employeeService.summary());
    }
}
