package com.example.cost.controller;

import com.example.cost.common.Result;
import com.example.cost.dto.ProjectCostQueryDTO;
import com.example.cost.dto.ProjectCostVO;
import com.example.cost.service.ProjectCostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cost")
@RequiredArgsConstructor
public class ProjectCostController {

    private final ProjectCostService projectCostService;

    @PostMapping("/project-stats")
    public Result<ProjectCostVO> queryProjectStats(@RequestBody ProjectCostQueryDTO query) {
        return Result.success(projectCostService.queryProjectStats(query));
    }
}