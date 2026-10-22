package com.dtazziboot.tools.controller;

import com.dtazziboot.tools.common.api.ApiResponse;
import com.dtazziboot.tools.model.dto.BubbleSortRequest;
import com.dtazziboot.tools.model.dto.BubbleSortResult;
import com.dtazziboot.tools.model.dto.HashRequest;
import com.dtazziboot.tools.model.dto.HashResult;
import com.dtazziboot.tools.model.dto.HelloRequest;
import com.dtazziboot.tools.model.dto.HelloResult;
import com.dtazziboot.tools.service.ToolService;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 算法工具接口（W01/W02/W03）。
 */
@Validated
@RestController
@RequestMapping("/api/tools")
public class ToolController {

    private final ToolService toolService;

    public ToolController(ToolService toolService) {
        this.toolService = toolService;
    }

    @PostMapping("/helloworld")
    public ApiResponse<HelloResult> helloworld(@RequestBody(required = false) HelloRequest request) {
        HelloRequest req = request == null ? new HelloRequest() : request;
        return ApiResponse.success(toolService.hello(req));
    }

    @PostMapping("/hash")
    public ApiResponse<HashResult> hash(@Valid @RequestBody HashRequest request) {
        return ApiResponse.success(toolService.hash(request.getText(), request.getAlgorithm()));
    }

    @PostMapping("/bubble-sort")
    public ApiResponse<BubbleSortResult> bubbleSort(@Valid @RequestBody BubbleSortRequest request) {
        return ApiResponse.success(toolService.bubbleSort(request.getNums(), request.getOrder()));
    }
}