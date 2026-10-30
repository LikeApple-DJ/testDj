package com.example.algorithm;

/**
 * 冒泡排序工具类.
 *
 * <p>提供 int 数组的升序排序，算法时间复杂度 O(n²)，空间复杂度 O(1)。</p>
 *
 * @author AiWork
 * @date 2026/10/30
 */
public final class BubbleSortUtil {

    private BubbleSortUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 对 int 数组进行升序冒泡排序（原地排序）.
     *
     * @param array 待排序数组，不能为 null
     * @throws IllegalArgumentException 当 array 为 null 时
     */
    public static void sort(int[] array) {
        if (array == null) {
            throw new IllegalArgumentException("array must not be null");
        }

        int length = array.length;

        for (int i = 0; i < length - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < length - 1 - i; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    swapped = true;
                }
            }

            // 本轮无交换说明已有序，提前退出
            if (!swapped) {
                break;
            }
        }
    }
}
