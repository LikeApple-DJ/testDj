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
import java.time.ZoneId;
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
    private static final ZoneId ZONE_SHANGHAI = ZoneId.of("Asia/Shanghai");

    /**
     * 按用户维度隔离的执行结果缓存（外层 key 为 callerId，内层 key 为接口类型）
     */
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> userResultCache =
            new ConcurrentHashMap<>();

    /**
     * 当前线程的调用者上下文（由 Controller 层设置）
     */
    private static final ThreadLocal<String> CURRENT_CALLER_ID = new ThreadLocal<>();

    public static final String CACHE_KEY_HELLOWORLD = "helloworld";
    public static final String CACHE_KEY_HASH = "hash";
    public static final String CACHE_KEY_BUBBLE_SORT = "bubble-sort";

    @Override
    public void setCallerContext(String callerId) {
        CURRENT_CALLER_ID.set(callerId != null ? callerId : "UNKNOWN");
    }

    @Override
    public void clearCallerContext() {
        CURRENT_CALLER_ID.remove();
    }

    @Override
    public HelloWorldDTO helloWorld(String name) {
        String actualName = (name == null || name.trim().isEmpty()) ? DEFAULT_NAME : name.trim();
        String result = "Hello, " + actualName + "!";
        String timestamp = LocalDateTime.now(ZONE_SHANGHAI).format(FORMATTER);
        HelloWorldDTO dto = new HelloWorldDTO(result, timestamp);
        putCacheResult(CACHE_KEY_HELLOWORLD, dto);
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
            String timestamp = LocalDateTime.now(ZONE_SHANGHAI).format(FORMATTER);
            HashDTO dto = new HashDTO(input, HASH_ALGORITHM, hashValue, timestamp);
            putCacheResult(CACHE_KEY_HASH, dto);
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

        String timestamp = LocalDateTime.now(ZONE_SHANGHAI).format(FORMATTER);
        BubbleSortDTO dto = new BubbleSortDTO(original, sorted, timestamp);
        putCacheResult(CACHE_KEY_BUBBLE_SORT, dto);
        return dto;
    }

    @Override
    public Object getCachedResult(String cacheKey) {
        String callerId = CURRENT_CALLER_ID.get();
        if (callerId == null) {
            callerId = "UNKNOWN";
        }
        ConcurrentHashMap<String, Object> callerCache = userResultCache.get(callerId);
        if (callerCache == null) {
            return null;
        }
        return callerCache.get(cacheKey);
    }

    /**
     * 将执行结果缓存到当前用户的隔离空间
     *
     * @param cacheKey 缓存键
     * @param result   结果对象
     */
    private void putCacheResult(String cacheKey, Object result) {
        String callerId = CURRENT_CALLER_ID.get();
        if (callerId == null) {
            callerId = "UNKNOWN";
        }
        userResultCache.computeIfAbsent(callerId, k -> new ConcurrentHashMap<>())
                .put(cacheKey, result);
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
