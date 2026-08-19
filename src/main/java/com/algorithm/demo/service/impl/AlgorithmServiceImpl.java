package com.algorithm.demo.service.impl;

import com.algorithm.demo.common.BusinessException;
import com.algorithm.demo.service.AlgorithmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 算法服务实现类
 *
 * @author DTCoder
 */
@Service
public class AlgorithmServiceImpl implements AlgorithmService {

    private static final Logger log = LoggerFactory.getLogger(AlgorithmServiceImpl.class);
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public String hello() {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        log.info("执行 HelloWorld, timestamp={}", timestamp);
        return "Hello World [" + timestamp + "]";
    }

    @Override
    public String hash(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("输入字符串不能为空");
        }
        long startTime = System.currentTimeMillis();
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            String hashValue = bytesToHex(hashBytes);
            long cost = System.currentTimeMillis() - startTime;
            log.info("执行 SHA-256 哈希, inputLength={}, cost={}ms", input.length(), cost);
            return hashValue;
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 算法不可用", e);
            throw new BusinessException("ALGO_003", "哈希计算异常", e);
        }
    }

    @Override
    public List<Integer> bubbleSort(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            throw new IllegalArgumentException("排序列表不能为空");
        }
        if (numbers.size() > 10000) {
            throw new BusinessException("ALGO_006", "排序列表长度不能超过10000");
        }
        long startTime = System.currentTimeMillis();

        // 复制列表，不修改原始输入
        List<Integer> list = new ArrayList<>(numbers);
        int n = list.size();
        int swapCount = 0;

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (list.get(j) > list.get(j + 1)) {
                    int temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                    swapCount++;
                    swapped = true;
                }
            }
            // 优化：如果没有交换，说明已经有序
            if (!swapped) {
                break;
            }
        }

        long cost = System.currentTimeMillis() - startTime;
        log.info("执行冒泡排序, size={}, swapCount={}, cost={}ms", n, swapCount, cost);
        return list;
    }

    /**
     * 字节数组转十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
