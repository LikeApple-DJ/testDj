package com.example.demo.controller;

import com.example.demo.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam String type) throws Exception {
        byte[] excelData;
        String filename;

        switch (type) {
            case "hello":
                excelData = exportService.generateHelloExcel();
                filename = "HelloWorld.xlsx";
                break;
            case "hash":
                // 默认导出示例数据
                excelData = exportService.generateHashExcel("示例字符串", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
                filename = "SHA256Hash.xlsx";
                break;
            case "sort":
                int[] original = {64, 34, 25, 12, 22, 11, 90};
                int[] sorted = {11, 12, 22, 25, 34, 64, 90};
                excelData = exportService.generateSortExcel(original, sorted, 10);
                filename = "BubbleSort.xlsx";
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.ok().headers(headers).body(excelData);
    }
}