package com.dt.example.hello;

/**
 * HelloWorld 示例程序，提供问候语生成功能。
 *
 * @author DTCoder
 * @date 2025/08/17
 */
public class HelloWorld {

    /** 默认问候语 */
    private static final String DEFAULT_GREETING = "Hello, World!";

    /**
     * 返回默认问候语 "Hello, World!"。
     *
     * @return 默认问候语字符串
     */
    public String greet() {
        return DEFAULT_GREETING;
    }

    /**
     * 根据指定名称返回个性化问候语。
     *
     * @param name 被问候者名称，为 null 时回退到默认问候语
     * @return 格式为 "Hello, {name}!" 的问候语字符串
     */
    public String greet(String name) {
        if (name == null) {
            return DEFAULT_GREETING;
        }
        return "Hello, " + name + "!";
    }

    /**
     * 程序入口，输出默认问候语到标准输出。
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        HelloWorld helloWorld = new HelloWorld();
        System.out.println(helloWorld.greet());
    }
}