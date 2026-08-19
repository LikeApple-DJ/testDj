package com.example.algodemo.service.impl;

import com.example.algodemo.common.exception.AlgorithmErrorCode;
import com.example.algodemo.common.exception.BusinessException;
import com.example.algodemo.service.SortService;
import com.example.algodemo.service.model.SortResult;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * 排序服务实现。
 */
@Service
public class SortServiceImpl implements SortService {

    private static final int MAX_ARRAY_LENGTH = 10000;
    private static final String ORDER_ASC = "ASC";
    private static final String ORDER_DESC = "DESC";

    @Override
    public SortResult bubbleSort(int[] array, String order) {
        if (array == null || array.length == 0) {
            throw new BusinessException(AlgorithmErrorCode.ALG_003);
        }
        if (array.length > MAX_ARRAY_LENGTH) {
            throw new BusinessException(AlgorithmErrorCode.ALG_003);
        }
        String sortOrder = resolveOrder(order);
        int[] original = array.clone();
        int[] sorted = array.clone();
        bubbleSortInternal(sorted, sortOrder);
        SortResult result = new SortResult();
        result.setOriginalArray(original);
        result.setSortedArray(sorted);
        result.setOrder(sortOrder);
        return result;
    }

    private String resolveOrder(String order) {
        if (order == null || order.trim().isEmpty()) {
            return ORDER_ASC;
        }
        String normalized = order.trim().toUpperCase(Locale.ROOT);
        if (!ORDER_ASC.equals(normalized) && !ORDER_DESC.equals(normalized)) {
            throw new BusinessException(AlgorithmErrorCode.ALG_001);
        }
        return normalized;
    }

    private void bubbleSortInternal(int[] array, String order) {
        boolean asc = ORDER_ASC.equals(order);
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                boolean needSwap = asc ? array[j] > array[j + 1] : array[j] < array[j + 1];
                if (needSwap) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }
}
