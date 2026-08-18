package com.example.demo.service.impl;

import com.example.demo.common.exception.DemoException;
import com.example.demo.service.DemoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Demo 演示模块服务实现。
 */
@Service
public class DemoServiceImpl implements DemoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoServiceImpl.class);
    private static final String DEFAULT_ALGORITHM = "SHA-256";
    private static final int MAX_INPUT_LENGTH = 10000;
    private static final int MAX_ARRAY_LENGTH = 1000;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public String hello() {
        LOGGER.info("DemoService.hello invoked");
        return "Hello, World!";
    }

    @Override
    public String hash(String input, String algorithm) {
        if (input == null || input.isEmpty()) {
            throw new DemoException("DEMO_001", "输入不能为空");
        }
        if (input.length() > MAX_INPUT_LENGTH) {
            throw new DemoException("DEMO_001", "输入过长，最大长度为" + MAX_INPUT_LENGTH);
        }

        String resolvedAlgorithm = (algorithm == null || algorithm.isEmpty())
                ? DEFAULT_ALGORITHM : algorithm.toUpperCase();

        if (!"SHA-256".equals(resolvedAlgorithm) && !"MD5".equals(resolvedAlgorithm)) {
            throw new DemoException("DEMO_002", "不支持的哈希算法：" + algorithm);
        }

        try {
            MessageDigest md = MessageDigest.getInstance(resolvedAlgorithm);
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            LOGGER.info("DemoService.hash completed, algorithm={}, inputLength={}", resolvedAlgorithm, input.length());
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new DemoException("DEMO_005", "系统内部错误：" + e.getMessage());
        }
    }

    @Override
    public int[] bubbleSort(int[] array) {
        if (array == null || array.length == 0) {
            throw new DemoException("DEMO_001", "数组不能为空");
        }
        if (array.length > MAX_ARRAY_LENGTH) {
            throw new DemoException("DEMO_001", "数组过长，最大长度为" + MAX_ARRAY_LENGTH);
        }

        int[] result = Arrays.copyOf(array, array.length);
        int n = result.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (result[j] > result[j + 1]) {
                    int temp = result[j];
                    result[j] = result[j + 1];
                    result[j + 1] = temp;
                }
            }
        }

        LOGGER.info("DemoService.bubbleSort completed, arrayLength={}", array.length);
        return result;
    }

    @Override
    public byte[] export(String type, String format, Map<String, Object> data) {
        if (type == null || type.isEmpty()) {
            throw new DemoException("DEMO_001", "导出类型不能为空");
        }
        if (!"hello".equals(type) && !"hash".equals(type) && !"sort".equals(type)) {
            throw new DemoException("DEMO_001", "导出类型无效，仅支持 hello/hash/sort");
        }
        if (data == null || data.isEmpty()) {
            throw new DemoException("DEMO_001", "数据不能为空");
        }

        String resolvedFormat = (format == null || format.isEmpty()) ? "json" : format.toLowerCase();
        if (!"json".equals(resolvedFormat) && !"csv".equals(resolvedFormat)) {
            throw new DemoException("DEMO_004", "不支持的导出格式：" + format);
        }

        if ("json".equals(resolvedFormat)) {
            return exportAsJson(type, data);
        } else {
            return exportAsCsv(type, data);
        }
    }

    private byte[] exportAsJson(String type, Map<String, Object> data) {
        try {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("type", type);
            result.putAll(data);
            String json = OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(result);
            LOGGER.info("DemoService.export JSON, type={}", type);
            return json.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new DemoException("DEMO_005", "JSON 序列化失败：" + e.getMessage());
        }
    }

    private byte[] exportAsCsv(String type, Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        // CSV header
        boolean first = true;
        for (String key : data.keySet()) {
            if (!first) {
                sb.append(',');
            }
            sb.append(escapeCsv(key));
            first = false;
        }
        sb.append('\n');

        // CSV values
        first = true;
        for (Object value : data.values()) {
            if (!first) {
                sb.append(',');
            }
            String strValue = value == null ? "" : formatCsvValue(value);
            sb.append(escapeCsv(strValue));
            first = false;
        }

        LOGGER.info("DemoService.export CSV, type={}", type);
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String formatCsvValue(Object value) {
        if (value instanceof int[]) {
            return Arrays.toString((int[]) value);
        }
        return value.toString();
    }
}