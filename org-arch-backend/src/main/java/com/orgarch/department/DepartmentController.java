package com.orgarch.department;

import com.orgarch.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService deptService;

    public DepartmentController(DepartmentService deptService) {
        this.deptService = deptService;
    }

    @GetMapping("/tree")
    public ApiResponse<List<DepartmentTreeVo>> tree() {
        return ApiResponse.ok(deptService.buildTree());
    }

    @GetMapping("/{id}/children")
    public ApiResponse<List<DepartmentTreeVo>> children(@PathVariable Long id) {
        return ApiResponse.ok(deptService.findChildren(id));
    }

    @PutMapping("/{id}/move")
    public ApiResponse<Void> move(@PathVariable Long id, @RequestBody MoveRequest req) {
        deptService.move(id, req.getNewParentId());
        return ApiResponse.ok("移动成功", null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        deptService.delete(id);
        return ApiResponse.ok("删除成功", null);
    }
}
