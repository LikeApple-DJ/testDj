package com.algorithm.demo.controller;

import com.algorithm.demo.common.AlgorithmType;
import com.algorithm.demo.common.BusinessException;
import com.algorithm.demo.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 导出控制器
 *
 * @author DTCoder
 */
@RestController
@RequestMapping("/api/export")
public class ExportController {

    private static final Logger log = LoggerFactory.getLogger(ExportController.class);

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    /**
     * W04 结果导出
     *
     * @param type     算法类型：HELLO / HASH / SORT
     * @param input    输入参数（HASH 和 SORT 需要）
     * @param response HTTP 响应
     */
    @GetMapping("/result")
    public void exportResult(
            @RequestParam String type,
            @RequestParam(required = false) String input,
            HttpServletResponse response) {

        long startTime = System.currentTimeMillis();

        // 参数校验
        AlgorithmType algorithmType;
        try {
            algorithmType = AlgorithmType.fromName(type);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("EXPORT_001", e.getMessage());
        }

        // 获取文件名和内容
        String fileName = exportService.getFileName(algorithmType);
        byte[] csvBytes = exportService.exportResult(algorithmType, input);

        // 设置响应头
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");

        // 写入 BOM（确保 Excel 正确识别 UTF-8）
        try (OutputStream os = response.getOutputStream()) {
            os.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            os.write(csvBytes);
            os.flush();
        } catch (IOException e) {
            log.error("导出文件写入失败", e);
            throw new BusinessException("EXPORT_002", "导出文件生成失败");
        }

        long cost = System.currentTimeMillis() - startTime;
        log.info("接口 /api/export/result 调用, type={}, fileName={}, cost={}ms", type, fileName, cost);
    }
}
