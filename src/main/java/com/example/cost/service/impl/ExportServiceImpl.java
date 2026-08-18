package com.example.cost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.cost.entity.LaborCost;
import com.example.cost.entity.ProjectCost;
import com.example.cost.mapper.LaborCostMapper;
import com.example.cost.mapper.ProjectCostMapper;
import com.example.cost.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final LaborCostMapper laborCostMapper;
    private final ProjectCostMapper projectCostMapper;

    @Override
    public void export(Map<String, Object> params, HttpServletResponse response) {
        String exportType = (String) params.getOrDefault("exportType", "full");
        String periodType = (String) params.get("periodType");
        String periodValue = (String) params.get("periodValue");

        try (Workbook workbook = new XSSFWorkbook()) {
            if ("labor".equals(exportType) || "full".equals(exportType)) {
                createLaborSheet(workbook, periodType, periodValue);
            }
            if ("project".equals(exportType) || "full".equals(exportType)) {
                createProjectSheet(workbook, periodType, periodValue);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String filename = URLEncoder.encode("成本统计报表.xlsx", StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + filename);

            try (OutputStream os = response.getOutputStream()) {
                workbook.write(os);
            }
        } catch (Exception e) {
            throw new RuntimeException("导出失败", e);
        }
    }

    private void createLaborSheet(Workbook workbook, String periodType, String periodValue) {
        Sheet sheet = workbook.createSheet("人力成本");
        Row header = sheet.createRow(0);
        String[] headers = {"人员ID", "角色", "成本金额", "月份", "季度", "年度"};
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        LambdaQueryWrapper<LaborCost> wrapper = new LambdaQueryWrapper<>();
        applyLaborPeriod(wrapper, periodType, periodValue);
        List<LaborCost> records = laborCostMapper.selectList(wrapper);

        int rowIdx = 1;
        for (LaborCost lc : records) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(lc.getPersonnelId());
            row.createCell(1).setCellValue(lc.getRole());
            row.createCell(2).setCellValue(lc.getCostAmount().doubleValue());
            row.createCell(3).setCellValue(lc.getCostMonth());
            row.createCell(4).setCellValue(lc.getCostQuarter());
            row.createCell(5).setCellValue(lc.getCostYear());
        }
    }

    private void createProjectSheet(Workbook workbook, String periodType, String periodValue) {
        Sheet sheet = workbook.createSheet("项目成本");
        Row header = sheet.createRow(0);
        String[] headers = {"项目ID", "预算金额", "实际消耗", "预算占比", "预计超支", "月份", "季度", "年度"};
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        LambdaQueryWrapper<ProjectCost> wrapper = new LambdaQueryWrapper<>();
        applyProjectPeriod(wrapper, periodType, periodValue);
        List<ProjectCost> records = projectCostMapper.selectList(wrapper);

        int rowIdx = 1;
        for (ProjectCost pc : records) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(pc.getProjectId());
            row.createCell(1).setCellValue(pc.getBudgetAmount().doubleValue());
            row.createCell(2).setCellValue(pc.getActualAmount().doubleValue());
            BigDecimal ratio = pc.getBudgetAmount().compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ZERO
                    : pc.getActualAmount().divide(pc.getBudgetAmount(), 4, java.math.RoundingMode.HALF_UP);
            row.createCell(3).setCellValue(ratio.doubleValue());
            row.createCell(4).setCellValue(pc.getActualAmount().subtract(pc.getBudgetAmount()).doubleValue());
            row.createCell(5).setCellValue(pc.getCostMonth());
            row.createCell(6).setCellValue(pc.getCostQuarter());
            row.createCell(7).setCellValue(pc.getCostYear());
        }
    }

    private void applyLaborPeriod(LambdaQueryWrapper<LaborCost> w, String pt, String pv) {
        switch (pt) {
            case "month" -> w.eq(LaborCost::getCostMonth, pv);
            case "quarter" -> w.eq(LaborCost::getCostQuarter, pv);
            case "year" -> w.eq(LaborCost::getCostYear, pv);
        }
    }

    private void applyProjectPeriod(LambdaQueryWrapper<ProjectCost> w, String pt, String pv) {
        switch (pt) {
            case "month" -> w.eq(ProjectCost::getCostMonth, pv);
            case "quarter" -> w.eq(ProjectCost::getCostQuarter, pv);
            case "year" -> w.eq(ProjectCost::getCostYear, pv);
        }
    }
}