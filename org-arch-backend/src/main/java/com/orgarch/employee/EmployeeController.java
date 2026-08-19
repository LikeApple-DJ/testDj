package com.orgarch.employee;

import com.orgarch.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService empService;

    public EmployeeController(EmployeeService empService) {
        this.empService = empService;
    }

    @GetMapping("/check")
    public ApiResponse<Map<String, Object>> check(@RequestParam String field,
                                                   @RequestParam String value) {
        return ApiResponse.ok(empService.check(field, value));
    }

    @PostMapping
    public ApiResponse<EmployeeVo> create(@Valid @RequestBody EmployeeCreateRequest req) {
        return ApiResponse.ok(empService.create(req));
    }

    @GetMapping
    public ApiResponse<EmployeePageVo> list(@RequestParam(required = false) Long deptId,
                                            @RequestParam(required = false) String status,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.ok(empService.page(deptId, status, page, size));
    }

    @PostMapping("/{id}/transfer")
    public ApiResponse<String> transfer(@PathVariable Long id,
                                        @Valid @RequestBody TransferRequest req,
                                        @RequestHeader(value = "X-Operator", required = false) String operator) {
        empService.transfer(id, req, operator);
        return ApiResponse.ok("调动成功", "调动成功");
    }

    @PutMapping("/{id}/resign")
    public ApiResponse<String> resign(@PathVariable Long id,
                                      @Valid @RequestBody ResignRequest req) {
        empService.resign(id, req);
        return ApiResponse.ok("离职办理成功", "离职办理成功");
    }
}
