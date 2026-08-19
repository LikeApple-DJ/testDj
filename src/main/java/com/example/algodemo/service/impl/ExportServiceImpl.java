package com.example.algodemo.service.impl;

import com.example.algodemo.common.constant.ExportFormatEnum;
import com.example.algodemo.common.constant.ExportTypeEnum;
import com.example.algodemo.common.exception.AlgorithmErrorCode;
import com.example.algodemo.common.exception.BusinessException;
import com.example.algodemo.service.ExportService;
import com.example.algodemo.service.HashService;
import com.example.algodemo.service.HelloWorldService;
import com.example.algodemo.service.SortService;
import com.example.algodemo.service.model.ExportResult;
import com.example.algodemo.service.model.HashResult;
import com.example.algodemo.service.model.SortResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 导出服务实现。
 */
@Service
public class ExportServiceImpl implements ExportService {

    private static final String DEFAULT_FILE_NAME = "export";
    private static final String CONTENT_TYPE_CSV = "text/csv;charset=UTF-8";
    private static final String CONTENT_TYPE_JSON = "application/json";

    private final HelloWorldService helloWorldService;
    private final HashService hashService;
    private final SortService sortService;
    private final ObjectMapper objectMapper;

    public ExportServiceImpl(HelloWorldService helloWorldService,
                             HashService hashService,
                             SortService sortService,
                             ObjectMapper objectMapper) {
        this.helloWorldService = helloWorldService;
        this.hashService = hashService;
        this.sortService = sortService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ExportResult export(String type, String format, Map<String, Object> params) {
        ExportTypeEnum exportType = ExportTypeEnum.of(type);
        ExportFormatEnum exportFormat = ExportFormatEnum.of(format);

        if (params == null) {
            params = new LinkedHashMap<>();
        }

        Object data = computeExportData(exportType, params);
        return formatResult(exportType, exportFormat, data);
    }

    private Object computeExportData(ExportTypeEnum type, Map<String, Object> params) {
        return switch (type) {
            case HELLO -> {
                String name = getString(params, "name");
                Map<String, String> result = new LinkedHashMap<>();
                result.put("greeting", helloWorldService.sayHello(name));
                yield result;
            }
            case HASH -> {
                String algorithm = getString(params, "algorithm");
                String content = getString(params, "content");
                yield hashService.hash(algorithm, content);
            }
            case BUBBLE_SORT -> {
                int[] array = getIntArray(params, "array");
                String order = getString(params, "order");
                SortResult sortResult = sortService.bubbleSort(array, order);
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("originalArray", sortResult.getOriginalArray());
                result.put("sortedArray", sortResult.getSortedArray());
                result.put("order", sortResult.getOrder());
                yield result;
            }
        };
    }

    private ExportResult formatResult(ExportTypeEnum type, ExportFormatEnum format, Object data) {
        String filename = DEFAULT_FILE_NAME + "_" + type.getCode().toLowerCase();
        return switch (format) {
            case CSV -> {
                String content = toCsv(type, data);
                yield new ExportResult(filename + ".csv", content, CONTENT_TYPE_CSV);
            }
            case JSON -> {
                try {
                    String content = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
                    yield new ExportResult(filename + ".json", content, CONTENT_TYPE_JSON);
                } catch (JsonProcessingException e) {
                    throw new BusinessException(AlgorithmErrorCode.ALG_004);
                }
            }
        };
    }

    @SuppressWarnings("unchecked")
    private String toCsv(ExportTypeEnum type, Object data) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (data instanceof Map) {
            map = (Map<String, Object>) data;
        }
        return switch (type) {
            case HELLO -> "type,result\nhello," + map.getOrDefault("greeting", "") + "\n";
            case HASH -> {
                HashResult hashResult = (HashResult) data;
                yield "algorithm,content,digest\n"
                        + escapeCsv(hashResult.getAlgorithm()) + ","
                        + escapeCsv(hashResult.getContent()) + ","
                        + escapeCsv(hashResult.getDigest()) + "\n";
            }
            case BUBBLE_SORT -> {
                String original = java.util.Arrays.toString((int[]) map.get("originalArray"));
                String sorted = java.util.Arrays.toString((int[]) map.get("sortedArray"));
                yield "originalArray,sortedArray,order\n"
                        + escapeCsv(original) + ","
                        + escapeCsv(sorted) + ","
                        + escapeCsv(String.valueOf(map.get("order"))) + "\n";
            }
        };
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String text = value.replace("\"", "\"\"");
        if (text.contains(",") || text.contains("\n") || text.contains("\r")) {
            text = "\"" + text + "\"";
        }
        return text;
    }

    private String getString(Map<String, Object> params, String key) {
        Object value = params.get(key);
        return value == null ? null : value.toString();
    }

    private int[] getIntArray(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value instanceof List<?>) {
            List<?> list = (List<?>) value;
            int[] result = new int[list.size()];
            for (int i = 0; i < list.size(); i++) {
                Object item = list.get(i);
                if (item instanceof Number) {
                    result[i] = ((Number) item).intValue();
                } else {
                    throw new BusinessException(AlgorithmErrorCode.ALG_003);
                }
            }
            return result;
        }
        if (value instanceof int[]) {
            return (int[]) value;
        }
        throw new BusinessException(AlgorithmErrorCode.ALG_003);
    }
}
