package com.dt.example.algorithm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HashService 测试")
class HashServiceTest {

    private final HashService hashService = new HashService();

    @Test
    @DisplayName("abc 的 MD5 应为固定十六进制值")
    void should_hashMd5_when_inputGiven() {
        assertThat(hashService.md5Hex("abc"))
            .isEqualTo("900150983cd24fb0d6963f7d28e17f72");
    }

    @Test
    @DisplayName("空串的 MD5 应为固定十六进制值")
    void should_hashEmptyString() {
        assertThat(hashService.md5Hex(""))
            .isEqualTo("d41d8cd98f00b204e9800998ecf8427e");
    }
}