package com.example.demo.controller;

import com.example.demo.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api")
public class ExportController {

    private final ExportService exportService;

    // 构造器注入，替代 @Autowired 字段注入
    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam String type,
            @RequestParam(required = false) String input,
            @RequestParam(required = false) String hash,
            @RequestParam(required = false) String original,
            @RequestParam(required = false) String sorted,
            @RequestParam(required = false) Integer swaps) {
        try {
            byte[] excelData;
            String filename;

            switch (type) {
                case "hello":
                    excelData = exportService.generateHelloExcel();
                    filename = "HelloWorld.xlsx";
                    break;
                case "hash":
                    // 支持前端传入当前展示的哈希数据，而非硬编码示例
                    String hashInput = (input != null) ? input : "示例字符串";
                    String hashValue = (hash != null) ? hash : "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
                    excelData = exportService.generateHashExcel(hashInput, hashValue);
                    filename = "SHA256Hash.xlsx";
                    break;
                case "sort":
                    // 支持前端传入当前展示的排序数据，而非硬编码示例
                    int[] originalArr = (original != null)
                            ? Arrays.stream(original.split(",")).mapToInt(Integer::parseInt).toArray()
                            : new int[]{64, 34, 25, 12, 22, 11, 90};
                    int[] sortedArr = (sorted != null)
                            ? Arrays.stream(sorted.split(",")).mapToInt(Integer::parseInt).toArray()
                            : new int[]{11, 12, 22, 25, 34, 64, 90};
                    int swapCount = (swaps != null) ? swaps : 10;
                    excelData = exportService.generateSortExcel(originalArr, sortedArr, swapCount);
                    filename = "BubbleSort.xlsx";
                    break;
                default:
                    return ResponseEntity.badRequest().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", filename);

            return ResponseEntity.ok().headers(headers).body(excelData);
        } catch (Exception e) {
            // 捕获具体异常，避免 throws Exception 传播到框架
            return ResponseEntity.internalServerError().build();
        }
    }
}