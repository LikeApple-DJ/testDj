package com.example.sort;

import java.util.Comparator;
import java.util.Objects;

/**
 * 冒泡排序工具类。
 *
 * <p>提供原地（in-place）冒泡排序实现，支持 {@code int[]} 基本类型数组和泛型对象数组。
 * 时间复杂度 O(n²)，空间复杂度 O(1)。当某轮遍历无元素交换时提前终止，最优情况（已有序）为 O(n)。
 *
 * <p>此类为纯工具类，不可实例化。
 *
 * <pre>{@code
 * // 基本类型排序
 * int[] arr = {5, 3, 8, 1, 2};
 * BubbleSort.sort(arr);
 *
 * // 泛型排序
 * String[] words = {"delta", "alpha", "charlie"};
 * BubbleSort.sort(words, Comparator.naturalOrder());
 * }</pre>
 *
 * @author AiWork
 * @since 1.0.0
 */
public final class BubbleSort {

    private BubbleSort() {
        throw new IllegalStateException("Utility class should not be instantiated");
    }

    /**
     * 对 {@code int[]} 数组进行原地冒泡排序（升序）。
     *
     * @param array 待排序数组，不可为 {@code null}
     * @throws NullPointerException 当 {@code array} 为 {@code null} 时抛出
     */
    public static void sort(int[] array) {
        Objects.requireNonNull(array, "array must not be null");

        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (array[j] > array[j + 1]) {
                    swap(array, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    /**
     * 对泛型数组进行原地冒泡排序，按指定 {@link Comparator} 排序。
     *
     * @param <T>        数组元素类型
     * @param array      待排序数组，不可为 {@code null}
     * @param comparator 比较器，不可为 {@code null}
     * @throws NullPointerException 当任一参数为 {@code null} 时抛出
     */
    public static <T> void sort(T[] array, Comparator<? super T> comparator) {
        Objects.requireNonNull(array, "array must not be null");
        Objects.requireNonNull(comparator, "comparator must not be null");

        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (comparator.compare(array[j], array[j + 1]) > 0) {
                    swap(array, j, j + 1);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    private static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}