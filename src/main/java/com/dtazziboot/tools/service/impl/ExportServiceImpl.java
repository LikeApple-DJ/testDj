package com.dtazziboot.tools.service.impl;

import com.alibaba.excel.EasyExcel;
import com.dtazziboot.tools.common.constant.ErrorCodeConsts;
import com.dtazziboot.tools.common.enums.ExportTypeEnum;
import com.dtazziboot.tools.common.exception.BizException;
import com.dtazziboot.tools.model.dto.BubbleSortResult;
import com.dtazziboot.tools.model.dto.DistributionItem;
import com.dtazziboot.tools.model.dto.ExportFile;
import com.dtazziboot.tools.model.dto.ExportRequest;
import com.dtazziboot.tools.model.dto.HashResult;
import com.dtazziboot.tools.model.dto.HelloRequest;
import com.dtazziboot.tools.model.dto.HelloResult;
import com.dtazziboot.tools.model.dto.ReportQuery;
import com.dtazziboot.tools.model.dto.TrendPoint;
import com.dtazziboot.tools.service.ExportService;
import com.dtazziboot.tools.service.ReportService;
import com.dtazziboot.tools.service.ToolService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 导出服务实现：xlsx 使用 EasyExcel 流式写出，csv 手动拼接，均落内存字节流。
 */
@Service
public class ExportServiceImpl implements ExportService {

    private static final String CONTENT_XLSX =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private static final String CONTENT_CSV = "text/csv;charset=UTF-8";

    private final ToolService toolService;

    private final ReportService reportService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExportServiceImpl(ToolService toolService, ReportService reportService) {
        this.toolService = toolService;
        this.reportService = reportService;
    }

    @Override
    public ExportFile export(ExportRequest request) {
        ExportTypeEnum type = ExportTypeEnum.fromCode(request.getType());
        if (type == null) {
            throw new BizException(ErrorCodeConsts.ERR_EXPORT_TYPE,
                    "unsupported export type: " + request.getType(),
                    "导出类型仅支持 helloworld / hash / bubble-sort / report");
        }
        boolean csv = "csv".equalsIgnoreCase(request.getFormat());
        TableData table = buildTable(type, request.getParams());
        if (csv) {
            return new ExportFile(buildFileName(type, "csv"), CONTENT_CSV, toCsv(table));
        }
        return new ExportFile(buildFileName(type, "xlsx"), CONTENT_XLSX, toXlsx(table));
    }

    private TableData buildTable(ExportTypeEnum type, String paramsJson) {
        return switch (type) {
            case HELLOWORLD -> buildHelloTable(paramsJson);
            case HASH -> buildHashTable(paramsJson);
            case BUBBLE_SORT -> buildBubbleSortTable(paramsJson);
            case REPORT -> buildReportTable(paramsJson);
        };
    }

    private TableData buildHelloTable(String paramsJson) {
        HelloRequest request = parse(paramsJson, HelloRequest.class);
        if (request == null) {
            request = new HelloRequest();
        }
        HelloResult result = toolService.hello(request);
        return TableData.of(List.of("name", "message"),
                List.of(List.of(request.getName() == null ? "" : request.getName(), result.getMessage())));
    }

    private TableData buildHashTable(String paramsJson) {
        HashParams params = parse(paramsJson, HashParams.class);
        if (params == null || params.text == null) {
            throw new BizException(ErrorCodeConsts.ERR_TEXT_EMPTY, "hash text is empty", "请提供待哈希文本");
        }
        HashResult result = toolService.hash(params.text, params.algorithm);
        return TableData.of(List.of("algorithm", "text", "hash"),
                List.of(List.of(result.getAlgorithm(), params.text, result.getHash())));
    }

    private TableData buildBubbleSortTable(String paramsJson) {
        BubbleSortParams params = parse(paramsJson, BubbleSortParams.class);
        if (params == null || params.nums == null || params.nums.length == 0) {
            throw new BizException(ErrorCodeConsts.ERR_TEXT_EMPTY, "nums is empty", "请提供待排序数组");
        }
        BubbleSortResult result = toolService.bubbleSort(params.nums, params.order);
        List<List<String>> rows = new ArrayList<>();
        for (int i = 0; i < result.getSorted().length; i++) {
            rows.add(List.of(String.valueOf(i),
                    String.valueOf(result.getOriginal()[i]),
                    String.valueOf(result.getSorted()[i])));
        }
        return TableData.of(List.of("index", "original", "sorted", "swapCount"),
                appendTail(rows, result.getSwapCount()));
    }

    private TableData buildReportTable(String paramsJson) {
        ReportQuery query = parse(paramsJson, ReportQuery.class);
        if (query == null) {
            query = new ReportQuery();
        }
        List<List<String>> rows = new ArrayList<>();
        if (query.getDimension() == null || query.getDimension().trim().isEmpty()) {
            List<TrendPoint> trend = reportService.trend(query);
            for (TrendPoint point : trend) {
                rows.add(List.of(point.getTime(), String.valueOf(point.getCount())));
            }
            return TableData.of(List.of("time", "count"), rows);
        }
        List<DistributionItem> items = reportService.distribution(query);
        for (DistributionItem item : items) {
            rows.add(List.of(item.getKey(), String.valueOf(item.getCount())));
        }
        return TableData.of(List.of("key", "count"), rows);
    }

    private List<List<String>> appendTail(List<List<String>> rows, long swapCount) {
        List<List<String>> copied = new ArrayList<>(rows);
        copied.add(List.of("", "", "swapCount", String.valueOf(swapCount)));
        return copied;
    }

    private <T> T parse(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new BizException(ErrorCodeConsts.ERR_TEXT_EMPTY,
                    "invalid params json: " + json, "导出参数格式不正确");
        }
    }

    private byte[] toCsv(TableData table) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", table.header())).append('\n');
        for (List<String> row : table.rows()) {
            sb.append(String.join(",", row)).append('\n');
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private byte[] toXlsx(TableData table) {
        List<List<String>> head = table.header().stream().map(List::of).toList();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            EasyExcel.write(out).head(head).sheet("result").doWrite(table.rows());
            return out.toByteArray();
        } catch (Exception e) {
            throw new BizException(ErrorCodeConsts.ERR_SYSTEM, "export xlsx failed", "导出失败，请稍后重试");
        }
    }

    private String buildFileName(ExportTypeEnum type, String ext) {
        return "export_" + type.getCode() + "." + ext;
    }

    private record TableData(List<String> header, List<List<String>> rows) {
        static TableData of(List<String> header, List<List<String>> rows) {
            return new TableData(header, rows);
        }
    }

    private static class HashParams {
        private String text;
        private String algorithm;
    }

    private static class BubbleSortParams {
        private int[] nums;
        private String order;
    }
}