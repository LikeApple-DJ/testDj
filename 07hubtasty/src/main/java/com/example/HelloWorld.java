package com.example;

/**
 * Hello World 示例程序。
 *
 * <p>演示 Java 程序基本结构，输出经典的 "Hello, World!" 问候语。
 *
 * @author auto-generated
 * @since 2026-10-21
 */
public class HelloWorld {

    /** 默认问候消息。 */
    private static final String DEFAULT_MESSAGE = "Hello, World!";

    /**
     * 程序入口，向标准输出打印问候语。
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        System.out.println(getMessage());
    }

    /**
     * 获取默认问候消息。
     *
     * @return 问候语字符串
     */
    public static String getMessage() {
        return DEFAULT_MESSAGE;
    }
}