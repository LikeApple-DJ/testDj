package com.example.demo.service.impl;

import com.example.demo.model.vo.GreetingVO;
import com.example.demo.service.GreetingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link GreetingServiceImpl} 的单元测试。
 *
 * <p>遵循 AAA 模式，覆盖正常路径、边界值和异常路径。</p>
 */
@DisplayName("GreetingServiceImpl 单元测试")
class GreetingServiceImplTest {

    private GreetingService greetingService;

    @BeforeEach
    void setUp() {
        greetingService = new GreetingServiceImpl();
    }

    // ==================== greet 测试 ====================

    @Nested
    @DisplayName("greet 方法")
    class GreetMethod {

        @Test
        @DisplayName("正常路径：传入有效名称应返回个性化问候")
        void should_returnGreetingVO_when_validNameProvided() {
            // Arrange
            String name = "Alice";

            // Act
            GreetingVO result = greetingService.greet(name);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getMessage()).as("问候语内容").isEqualTo("Hello, Alice!");
            assertThat(result.getName()).as("用户名").isEqualTo("Alice");
            assertThat(result.getTimestamp()).as("时间戳不应为空").isNotNull();
        }

        @Test
        @DisplayName("正常路径：传入中文名称应正确处理")
        void should_returnGreetingVO_when_chineseNameProvided() {
            // Arrange
            String name = "世界";

            // Act
            GreetingVO result = greetingService.greet(name);

            // Assert
            assertThat(result.getMessage()).isEqualTo("Hello, 世界!");
            assertThat(result.getName()).isEqualTo("世界");
        }

        @Test
        @DisplayName("边界值：传入空字符串应返回默认问候")
        void should_returnDefaultGreeting_when_emptyName() {
            // Arrange
            String name = "";

            // Act
            GreetingVO result = greetingService.greet(name);

            // Assert
            assertThat(result.getMessage()).isEqualTo("Hello, Stranger!");
            assertThat(result.getName()).isEqualTo("Stranger");
        }

        @Test
        @DisplayName("边界值：传入仅空白字符应返回默认问候")
        void should_returnDefaultGreeting_when_blankName() {
            // Arrange
            String name = "   ";

            // Act
            GreetingVO result = greetingService.greet(name);

            // Assert
            assertThat(result.getMessage()).isEqualTo("Hello, Stranger!");
            assertThat(result.getName()).isEqualTo("Stranger");
        }

        @Test
        @DisplayName("异常路径：传入 null 应抛出异常")
        void should_throwException_when_nameIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> greetingService.greet(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("name must not be null");
        }
    }

    // ==================== greetWithFormat 测试 ====================

    @Nested
    @DisplayName("greetWithFormat 方法")
    class GreetWithFormatMethod {

        @Test
        @DisplayName("正常路径：传入名称和格式前缀应返回格式化问候")
        void should_returnFormattedGreeting_when_validPrefixAndName() {
            // Arrange
            String name = "Bob";
            String prefix = "Hi";

            // Act
            GreetingVO result = greetingService.greetWithFormat(name, prefix);

            // Assert
            assertThat(result.getMessage()).isEqualTo("Hi, Bob!");
            assertThat(result.getName()).isEqualTo("Bob");
        }

        @Test
        @DisplayName("边界值：格式前缀为 null 时使用默认前缀")
        void should_useDefaultPrefix_when_prefixIsNull() {
            // Arrange
            String name = "Charlie";

            // Act
            GreetingVO result = greetingService.greetWithFormat(name, null);

            // Assert
            assertThat(result.getMessage()).isEqualTo("Hello, Charlie!");
        }

        @Test
        @DisplayName("边界值：格式前缀为空字符串时使用默认前缀")
        void should_useDefaultPrefix_when_prefixIsEmpty() {
            // Arrange
            String name = "Diana";

            // Act
            GreetingVO result = greetingService.greetWithFormat(name, "");

            // Assert
            assertThat(result.getMessage()).isEqualTo("Hello, Diana!");
        }
    }
}