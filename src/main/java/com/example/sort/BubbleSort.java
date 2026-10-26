package com.example.sort;

/**
 * 冒泡排序算法实现。
 *
 * <p>核心思想：重复遍历待排序序列，依次比较相邻元素，
 * 若顺序错误则交换，每次遍历将当前未排序部分的最大值"浮"到末尾。
 *
 * <p>时间复杂度：O(n²) 平均/最坏，O(n) 最好（已有序且启用提前终止）。
 * 空间复杂度：O(1)，原地排序，稳定。
 */
public final class BubbleSort {

    private BubbleSort() {
        // 工具类禁止实例化
    }

    /**
     * 对整数数组进行升序冒泡排序（原地修改）。
     *
     * @param arr 待排序数组，不可为 null
     * @throws NullPointerException 若 arr 为 null
     */
    public static void sort(int[] arr) {
        if (arr == null) {
            throw new NullPointerException("输入数组不可为 null");
        }

        int n = arr.length;
        if (n <= 1) {
            return; // 空数组或单元素已有序
        }

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false; // 提前终止标志

            // 每轮将最大值冒泡到 arr[n - 1 - i]
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                    swapped = true;
                }
            }

            // 若本轮无交换，数组已完全有序，提前结束
            if (!swapped) {
                break;
            }
        }
    }

    /**
     * 交换数组两个下标的元素。
     */
    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}