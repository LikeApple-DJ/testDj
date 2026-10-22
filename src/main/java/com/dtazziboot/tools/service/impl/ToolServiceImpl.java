package com.dtazziboot.tools.service.impl;

import com.dtazziboot.tools.common.constant.ErrorCodeConsts;
import com.dtazziboot.tools.common.enums.AlgorithmEnum;
import com.dtazziboot.tools.common.enums.SortOrderEnum;
import com.dtazziboot.tools.common.exception.BizException;
import com.dtazziboot.tools.model.dto.BubbleSortResult;
import com.dtazziboot.tools.model.dto.HashResult;
import com.dtazziboot.tools.model.dto.HelloRequest;
import com.dtazziboot.tools.model.dto.HelloResult;
import com.dtazziboot.tools.service.ToolService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 算法工具服务实现。
 */
@Service
public class ToolServiceImpl implements ToolService {

    private static final String DEFAULT_NAME = "World";

    @Override
    public HelloResult hello(HelloRequest request) {
        String name = request.getName();
        if (name == null || name.trim().isEmpty()) {
            name = DEFAULT_NAME;
        }
        return new HelloResult("Hello, " + name);
    }

    @Override
    public HashResult hash(String text, String algorithm) {
        AlgorithmEnum algorithmEnum = AlgorithmEnum.fromName(algorithm);
        if (algorithmEnum == null) {
            throw new BizException(ErrorCodeConsts.ERR_ALGORITHM,
                    "unsupported algorithm: " + algorithm, "仅支持 MD5 或 SHA256");
        }
        if (text == null || text.isEmpty()) {
            throw new BizException(ErrorCodeConsts.ERR_TEXT_EMPTY,
                    "text is empty", "待哈希文本不能为空");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithmEnum.getJcaName());
            byte[] bytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return new HashResult(algorithmEnum.name(), toHex(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new BizException(ErrorCodeConsts.ERR_SYSTEM,
                    "algorithm unavailable: " + algorithmEnum.getJcaName(), "服务暂时不可用");
        }
    }

    @Override
    public BubbleSortResult bubbleSort(int[] nums, String order) {
        SortOrderEnum sortOrder = resolveOrder(order);
        int[] original = Arrays.copyOf(nums, nums.length);
        int[] sorted = Arrays.copyOf(nums, nums.length);
        long swapCount = 0L;
        for (int i = 0; i < sorted.length - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < sorted.length - 1 - i; j++) {
                boolean needSwap = sortOrder == SortOrderEnum.ASC
                        ? sorted[j] > sorted[j + 1]
                        : sorted[j] < sorted[j + 1];
                if (needSwap) {
                    int temp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = temp;
                    swapCount++;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
        BubbleSortResult result = new BubbleSortResult();
        result.setOriginal(original);
        result.setSorted(sorted);
        result.setSwapCount(swapCount);
        result.setOrder(sortOrder.name());
        return result;
    }

    private SortOrderEnum resolveOrder(String order) {
        if (order == null || order.trim().isEmpty()) {
            return SortOrderEnum.ASC;
        }
        SortOrderEnum parsed = SortOrderEnum.fromName(order);
        if (parsed == null) {
            throw new BizException(ErrorCodeConsts.ERR_ORDER,
                    "unsupported order: " + order, "排序方向仅支持 ASC 或 DESC");
        }
        return parsed;
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}