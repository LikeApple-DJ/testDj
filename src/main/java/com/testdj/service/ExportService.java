package com.testdj.service;

import com.testdj.entity.ApiCallLog;
import com.testdj.repository.ApiCallLogRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {

    private final ApiCallLogRepository repository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public ExportService(ApiCallLogRepository repository) {
        this.repository = repository;
    }

    /**
     * Export call logs for a given API name as CSV content.
     */
    public String exportCsv(String apiName) {
        List<ApiCallLog> logs = repository.findByApiName(apiName);
        StringBuilder sb = new StringBuilder();
        sb.append("id,api_name,caller,department,level,type,call_time\n");
        for (ApiCallLog log : logs) {
            sb.append(log.getId()).append(",");
            sb.append(escapeCsv(log.getApiName())).append(",");
            sb.append(escapeCsv(log.getCaller())).append(",");
            sb.append(escapeCsv(log.getDepartment())).append(",");
            sb.append(escapeCsv(log.getLevel())).append(",");
            sb.append(escapeCsv(log.getType())).append(",");
            sb.append(log.getCallTime() != null ? log.getCallTime().format(FORMATTER) : "").append("\n");
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