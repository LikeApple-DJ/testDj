package com.algorithm.demo.service.impl;

import com.algorithm.demo.common.AlgorithmType;
import com.algorithm.demo.common.BusinessException;
import com.algorithm.demo.service.AlgorithmService;
import com.algorithm.demo.service.ExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 导出服务实现类
 *
 * @author DTCoder
 */
@Service
public class ExportServiceImpl implements ExportService {

    private static final Logger log = LoggerFactory.getLogger(ExportServiceImpl.class);
    private static final DateTimeFormatter FILE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final AlgorithmService algorithmService;

    public ExportServiceImpl(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    @Override
    public byte[] exportResult(AlgorithmType type, String input) {
        long startTime = System.currentTimeMillis();
        String csvContent;

        switch (type) {
            case HELLO:
                csvContent = buildHelloCsv();
                break;
            case HASH:
                if (input == null || input.isBlank()) {
                    throw new IllegalArgumentException("HASH 类型导出必须提供 input 参数");
                }
                csvContent = buildHashCsv(input);
                break;
            case SORT:
                if (input == null || input.isBlank()) {
                    throw new IllegalArgumentException("SORT 类型导出必须提供 input 参数");
                }
                csvContent = buildSortCsv(input);
                break;
            default:
                throw new BusinessException("EXPORT_001", "不支持的算法类型: " + type);
        }

        long cost = System.currentTimeMillis() - startTime;
        log.info("导出 {} 结果, cost={}ms", type.getDescription(), cost);
        return csvContent.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String getFileName(AlgorithmType type) {
        String timestamp = LocalDateTime.now().format(FILE_FORMATTER);
        return type.name().toLowerCase() + "_result_" + timestamp + ".csv";
    }

    /**
     * 构建 HelloWorld CSV 内容
     */
    private String buildHelloCsv() {
        String result = algorithmService.hello();
        StringBuilder sb = new StringBuilder();
        sb.append("类型,消息\n");
        sb.append("HelloWorld,").append(escapeCsv(result)).append("\n");
        return sb.toString();
    }

    /**
     * 构建哈希 CSV 内容
     */
    private String buildHashCsv(String input) {
        String hashValue = algorithmService.hash(input);
        StringBuilder sb = new StringBuilder();
        sb.append("输入,算法,哈希值\n");
        sb.append(escapeCsv(input)).append(",SHA-256,").append(hashValue).append("\n");
        return sb.toString();
    }

    /**
     * 构建排序 CSV 内容
     */
    private String buildSortCsv(String input) {
        List<Integer> numbers = parseNumbers(input);
        List<Integer> sorted = algorithmService.bubbleSort(numbers);
        StringBuilder sb = new StringBuilder();
        sb.append("原始列表,排序结果\n");
        sb.append(escapeCsv(numbers.toString())).append(",").append(escapeCsv(sorted.toString())).append("\n");
        return sb.toString();
    }

    /**
     * 解析逗号分隔的数字字符串
     */
    private List<Integer> parseNumbers(String input) {
        try {
            return Arrays.stream(input.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("排序输入格式错误，请使用逗号分隔的整数，如: 5,3,8,1,2");
        }
    }

    /**
     * CSV 字段转义（包含逗号或引号时用双引号包裹）
     */
    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
