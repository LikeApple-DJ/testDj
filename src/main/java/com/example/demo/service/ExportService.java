package com.example.demo.service;

import com.example.demo.entity.ApiCallLog;
import com.example.demo.exception.BusinessException;
import com.example.demo.repository.ApiCallLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExportService {

    private static final int MAX_EXPORT_SIZE = 10000;

    private final ApiCallLogRepository repository;

    public ExportService(ApiCallLogRepository repository) {
        this.repository = repository;
    }

    public String exportToCsv(String type) {
        List<ApiCallLog> logs = repository.findByApiNameOrderByCallTimeDesc(type);

        // Limit to MAX_EXPORT_SIZE records
        if (logs.size() > MAX_EXPORT_SIZE) {
            logs = logs.subList(0, MAX_EXPORT_SIZE);
        }

        StringBuilder sb = new StringBuilder();
        // BOM for UTF-8
        sb.append('\ufeff');

        // Header based on type
        switch (type) {
            case "hello":
                sb.append("call_time,user_id,user_type,user_level,user_dept,name,message\n");
                for (ApiCallLog log : logs) {
                    sb.append(escapeCsv(log.getCallTime()))
                      .append(",").append(escapeCsv(log.getUserId()))
                      .append(",").append(escapeCsv(log.getUserType()))
                      .append(",").append(escapeCsv(log.getUserLevel()))
                      .append(",").append(escapeCsv(log.getUserDept()))
                      .append(",").append(escapeCsv(log.getRequestBody()))
                      .append(",").append(escapeCsv(log.getResponseBody()))
                      .append("\n");
                }
                break;
            case "hash":
                sb.append("call_time,user_id,user_type,user_level,user_dept,input,algorithm,hash\n");
                for (ApiCallLog log : logs) {
                    sb.append(escapeCsv(log.getCallTime()))
                      .append(",").append(escapeCsv(log.getUserId()))
                      .append(",").append(escapeCsv(log.getUserType()))
                      .append(",").append(escapeCsv(log.getUserLevel()))
                      .append(",").append(escapeCsv(log.getUserDept()))
                      .append(",").append(escapeCsv(log.getRequestBody()))
                      .append(",").append(escapeCsv(log.getResponseBody()))
                      .append("\n");
                }
                break;
            case "bubble-sort":
                sb.append("call_time,user_id,user_type,user_level,user_dept,array,sorted,steps\n");
                for (ApiCallLog log : logs) {
                    sb.append(escapeCsv(log.getCallTime()))
                      .append(",").append(escapeCsv(log.getUserId()))
                      .append(",").append(escapeCsv(log.getUserType()))
                      .append(",").append(escapeCsv(log.getUserLevel()))
                      .append(",").append(escapeCsv(log.getUserDept()))
                      .append(",").append(escapeCsv(log.getRequestBody()))
                      .append(",").append(escapeCsv(log.getResponseBody()))
                      .append("\n");
                }
                break;
            default:
                throw new BusinessException("EXPORT_001", "type 参数不合法，必须为 hello/hash/bubble-sort");
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
