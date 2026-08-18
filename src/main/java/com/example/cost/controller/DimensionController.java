package com.example.cost.controller;

import com.example.cost.common.DimensionVO;
import com.example.cost.common.Result;
import com.example.cost.service.DimensionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cost")
@RequiredArgsConstructor
public class DimensionController {

    private final DimensionService dimensionService;

    @GetMapping("/dimensions")
    public Result<DimensionVO> getDimensions() {
        return Result.success(dimensionService.getDimensions());
    }
}