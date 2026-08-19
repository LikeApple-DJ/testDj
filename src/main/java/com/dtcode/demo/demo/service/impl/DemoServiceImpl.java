package com.dtcode.demo.demo.service.impl;

import com.dtcode.demo.common.exception.BusinessException;
import com.dtcode.demo.demo.model.dto.BubbleSortDTO;
import com.dtcode.demo.demo.model.dto.HashDTO;
import com.dtcode.demo.demo.model.dto.HelloWorldDTO;
import com.dtcode.demo.demo.service.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 演示接口业务服务实现
 *
 * @author DTCoder
 */
@Service
public class DemoServiceImpl implements DemoService {

    private static final Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);

    private static final String DEFAULT_NAME = "World";
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int MAX_ARRAY_SIZE = 10000;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 最近执行结果缓存（按接口类型缓存，用于导出）
     */
    private final ConcurrentHashMap<String, Object> resultCache = new ConcurrentHashMap<>();

    public static final String CACHE_KEY_HELLOWORLD = "helloworld";
    public static final String CACHE_KEY_HASH = "hash";
    public static final String CACHE_KEY_BUBBLE_SORT = "bubble-sort";

    @Override
    public HelloWorldDTO helloWorld(String name) {
        String actualName = (name == null || name.trim().isEmpty()) ? DEFAULT_NAME : name.trim();
        String result = "Hello, " + actualName + "!";
        String timestamp = LocalDateTime.now().format(FORMATTER);
        HelloWorldDTO dto = new HelloWorldDTO(result, timestamp);
        resultCache.put(CACHE_KEY_HELLOWORLD, dto);
        return dto;
    }

    @Override
    public HashDTO hash(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new BusinessException("DEMO_002", "输入不能为空");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            String hashValue = bytesToHex(hashBytes);
            String timestamp = LocalDateTime.now().format(FORMATTER);
            HashDTO dto = new HashDTO(input, HASH_ALGORITHM, hashValue, timestamp);
            resultCache.put(CACHE_KEY_HASH, dto);
            return dto;
        } catch (NoSuchAlgorithmException e) {
            logger.error("SHA-256算法不可用: {}", e.getMessage(), e);
            throw new BusinessException("DEMO_003", "哈希算法不可用", e);
        }
    }

    @Override
    public BubbleSortDTO bubbleSort(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            throw new BusinessException("DEMO_004", "输入数组不能为空");
        }
        if (numbers.size() > MAX_ARRAY_SIZE) {
            throw new BusinessException("DEMO_005", "数组长度超过上限（" + MAX_ARRAY_SIZE + "）");
        }

        List<Integer> original = Collections.unmodifiableList(new ArrayList<>(numbers));
        List<Integer> sorted = new ArrayList<>(numbers);

        // 经典冒泡排序
        int size = sorted.size();
        for (int i = 0; i < size - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < size - 1 - i; j++) {
                if (sorted.get(j) > sorted.get(j + 1)) {
                    Integer temp = sorted.get(j);
                    sorted.set(j, sorted.get(j + 1));
                    sorted.set(j + 1, temp);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }

        String timestamp = LocalDateTime.now().format(FORMATTER);
        BubbleSortDTO dto = new BubbleSortDTO(original, sorted, timestamp);
        resultCache.put(CACHE_KEY_BUBBLE_SORT, dto);
        return dto;
    }

    /**
     * 获取最近执行结果缓存
     *
     * @param cacheKey 缓存键
     * @return 缓存的结果对象，不存在返回null
     */
    public Object getCachedResult(String cacheKey) {
        return resultCache.get(cacheKey);
    }

    /**
     * 字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
