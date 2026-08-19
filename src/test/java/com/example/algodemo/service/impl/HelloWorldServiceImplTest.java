package com.example.algodemo.service.impl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HelloWorldServiceImplTest {

    private final HelloWorldServiceImpl helloWorldService = new HelloWorldServiceImpl();

    @Test
    void should_returnGreeting_when_nameProvided() {
        // Act
        String result = helloWorldService.sayHello("Alice");

        // Assert
        assertThat(result).isEqualTo("Hello, Alice!");
    }

    @Test
    void should_returnDefaultGreeting_when_nameIsNull() {
        // Act
        String result = helloWorldService.sayHello(null);

        // Assert
        assertThat(result).isEqualTo("Hello, World!");
    }

    @Test
    void should_returnDefaultGreeting_when_nameIsBlank() {
        // Act
        String result = helloWorldService.sayHello("   ");

        // Assert
        assertThat(result).isEqualTo("Hello, World!");
    }
}
