package com.example.cost.controller;

import com.example.cost.common.Result;
import com.example.cost.dto.LaborCostQueryDTO;
import com.example.cost.dto.LaborCostVO;
import com.example.cost.service.LaborCostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cost")
@RequiredArgsConstructor
public class LaborCostController {

    private final LaborCostService laborCostService;

    @PostMapping("/labor-stats")
    public Result<LaborCostVO> queryLaborStats(@RequestBody LaborCostQueryDTO query) {
        return Result.success(laborCostService.queryLaborStats(query));
    }
}