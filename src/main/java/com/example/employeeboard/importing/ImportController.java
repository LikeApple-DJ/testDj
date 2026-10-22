package com.example.employeeboard.importing;

import com.example.employeeboard.common.Result;
import com.example.employeeboard.common.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 批量导入管理端接口（W07-W09）。
 */
@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    /**
     * W07 模板下载：列 工号*、姓名*、部门*、岗位、邮箱、手机号、入职日期、成本预算金额、预算周期。
     */
    @GetMapping("/template")
    public void downloadTemplate(jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        try (org.apache.poi.ss.usermodel.Workbook workbook =
                     new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("员工导入模板");
            org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
            String[] columns = {"工号", "姓名", "部门", "岗位", "邮箱", "手机号", "入职日期", "成本预算金额", "预算周期"};
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=employee_import_template.xlsx");
            workbook.write(response.getOutputStream());
        }
    }

    @PostMapping("/upload")
    public Result<ImportTaskDTO> upload(@RequestParam("file") MultipartFile file,
                                        @RequestParam(value = "overwrite", defaultValue = "false") boolean overwrite) {
        return Result.ok(importService.importEmployees(file, UserContext.getCurrentEmpNo(), overwrite));
    }

    @GetMapping("/taskDetail")
    public Result<ImportTaskDTO> taskDetail(@RequestParam Long taskId,
                                            @RequestParam(required = false) String result) {
        return Result.ok(importService.getTaskDetail(taskId, result));
    }
}
