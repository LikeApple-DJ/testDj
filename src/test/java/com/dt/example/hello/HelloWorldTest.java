package com.dt.example.hello;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HelloWorld 单元测试
 *
 * @author DTCoder
 * @date 2025/08/17
 */
@DisplayName("HelloWorld 测试")
class HelloWorldTest {

    // ==================== greet 测试 ====================

    /**
     * 正常路径：调用 greet() 返回默认问候语 "Hello, World!"
     */
    @Test
    @DisplayName("调用 greet() 应返回默认问候语")
    void should_returnDefaultGreeting_when_called() {
        // Arrange
        HelloWorld helloWorld = new HelloWorld();

        // Act
        String result = helloWorld.greet();

        // Assert
        assertThat(result).isEqualTo("Hello, World!");
    }

    /**
     * 带参数路径：调用 greet(name) 返回个性化问候语
     */
    @Test
    @DisplayName("调用 greet(name) 应返回个性化问候语")
    void should_returnPersonalizedGreeting_when_nameProvided() {
        // Arrange
        HelloWorld helloWorld = new HelloWorld();

        // Act
        String result = helloWorld.greet("DTCoder");

        // Assert
        assertThat(result).isEqualTo("Hello, DTCoder!");
    }

    /**
     * 边界条件：name 为空字符串时正确处理
     */
    @Test
    @DisplayName("名称为空字符串时应返回仅含感叹号的问候语")
    void should_returnGreetingWithExclamation_when_nameIsEmpty() {
        // Arrange
        HelloWorld helloWorld = new HelloWorld();

        // Act
        String result = helloWorld.greet("");

        // Assert
        assertThat(result).isEqualTo("Hello, !");
    }

    /**
     * 边界条件：name 为 null 时回退到默认问候语
     */
    @Test
    @DisplayName("名称为 null 时应回退到默认问候语")
    void should_returnDefaultGreeting_when_nameIsNull() {
        // Arrange
        HelloWorld helloWorld = new HelloWorld();

        // Act
        String result = helloWorld.greet(null);

        // Assert
        assertThat(result).isEqualTo("Hello, World!");
    }
}