package com.example.demo.service;

import com.example.demo.model.CallRecord;
import com.example.demo.repository.CallRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExportService {

    @Autowired
    private CallRecordRepository callRecordRepository;

    public String exportCsv(String tab) {
        String apiPattern = switch (tab) {
            case "hello" -> "hello%";
            case "hash" -> "hash%";
            case "sort" -> "sort%";
            default -> "%";
        };

        List<CallRecord> records = callRecordRepository.findByApiNameLike(apiPattern);

        StringBuilder sb = new StringBuilder();
        sb.append("ID,CallerID,CallerType,CallerLevel,CallerDept,ApiName,CallTime,ResponseTime(ms)\n");

        for (CallRecord r : records) {
            sb.append(r.getId()).append(",");
            sb.append(escapeCsv(r.getCallerId())).append(",");
            sb.append(escapeCsv(r.getCallerType())).append(",");
            sb.append(escapeCsv(r.getCallerLevel())).append(",");
            sb.append(escapeCsv(r.getCallerDept())).append(",");
            sb.append(escapeCsv(r.getApiName())).append(",");
            sb.append(r.getCallTime() != null ? r.getCallTime().toString() : "").append(",");
            sb.append(r.getResponseTime()).append("\n");
        }

        return sb.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}