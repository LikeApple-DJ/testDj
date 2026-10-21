package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link HelloWorld} 的单元测试。
 */
@DisplayName("HelloWorld 单元测试")
class HelloWorldTest {

    // ==================== main 测试 ====================

    @Test
    @DisplayName("应输出 Hello, World! 到标准输出")
    void should_printHelloWorld_when_mainExecuted() {
        // Arrange
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            // Act
            HelloWorld.main(new String[]{});
        } finally {
            // Restore
            System.setOut(originalOut);
        }

        // Assert
        assertThat(outContent.toString().trim())
                .isEqualTo("Hello, World!");
    }

    // ==================== getMessage 测试 ====================

    @Test
    @DisplayName("应返回 Hello, World! 字符串")
    void should_returnHelloWorldString_when_getMessageCalled() {
        // Arrange & Act
        String message = HelloWorld.getMessage();

        // Assert
        assertThat(message).isEqualTo("Hello, World!");
    }
}