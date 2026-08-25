package com.example.demo.service;
import com.example.demo.model.TrackingRecord;
import com.example.demo.model.User;
import com.example.demo.repository.TrackingRecordRepository;
import com.example.demo.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.List;
@Service
public class ExportService {
    private final TrackingRecordRepository trackingRepo;
    private final UserRepository userRepository;
    public ExportService(TrackingRecordRepository trackingRepo, UserRepository userRepository) {
        this.trackingRepo = trackingRepo;
        this.userRepository = userRepository;
    }
    public byte[] generateExcel(String apiName) {
        List<TrackingRecord> records = trackingRepo.findByApiName(apiName);
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(apiName + "_export");
            Row header = sheet.createRow(0);
            String[] columns = {"调用人", "人员类型", "人员层级", "人员部门", "接口名", "请求参数", "调用时间", "IP地址"};
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            int rowNum = 1;
            for (TrackingRecord record : records) {
                Row row = sheet.createRow(rowNum++);
                User user = userRepository.findById(record.getUserId()).orElse(null);
                row.createCell(0).setCellValue(user != null ? user.getUsername() : "N/A");
                row.createCell(1).setCellValue(user != null ? user.getPersonType() : "");
                row.createCell(2).setCellValue(user != null ? user.getPersonLevel() : "");
                row.createCell(3).setCellValue(user != null ? user.getPersonDept() : "");
                row.createCell(4).setCellValue(record.getApiName());
                row.createCell(5).setCellValue(record.getParamsJson());
                row.createCell(6).setCellValue(record.getCallTime() != null ? record.getCallTime().toString() : "");
                row.createCell(7).setCellValue(record.getIpAddress());
            }
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Excel generation failed", e);
        }
    }
}