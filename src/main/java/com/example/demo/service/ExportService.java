package com.example.demo.service;

import com.example.demo.entity.TrackingRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {

    @Autowired
    private TrackingService trackingService;

    public byte[] exportCsv(String tab, String callerName) {
        List<TrackingRecord> records;

        if (callerName != null && !callerName.isBlank()) {
            records = trackingService.getRecordsByApiNameAndCallerName(tab, callerName);
        } else {
            records = trackingService.getRecordsByApiName(tab);
        }

        StringBuilder sb = new StringBuilder();

        // CSV header
        sb.append("ID,API Name,Caller Name,Caller Type,Caller Level,Caller Dept,Extra Info,Call Time\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (TrackingRecord record : records) {
            sb.append(record.getId()).append(",");
            sb.append(escapeCsv(record.getApiName())).append(",");
            sb.append(escapeCsv(record.getCallerName())).append(",");
            sb.append(escapeCsv(record.getCallerType())).append(",");
            sb.append(escapeCsv(record.getCallerLevel())).append(",");
            sb.append(escapeCsv(record.getCallerDept())).append(",");
            sb.append(escapeCsv(record.getExtraInfo())).append(",");
            sb.append(record.getCallTime() != null ? record.getCallTime().format(formatter) : "").append("\n");
        }

        // UTF-8 BOM
        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] csvBytes = sb.toString().getBytes(StandardCharsets.UTF_8);

        byte[] result = new byte[bom.length + csvBytes.length];
        System.arraycopy(bom, 0, result, 0, bom.length);
        System.arraycopy(csvBytes, 0, result, bom.length, csvBytes.length);

        return result;
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}