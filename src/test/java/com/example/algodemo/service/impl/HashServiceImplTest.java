package com.example.algodemo.service.impl;

import com.example.algodemo.common.exception.BusinessException;
import com.example.algodemo.service.model.HashResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HashServiceImplTest {

    private final HashServiceImpl hashService = new HashServiceImpl();

    @Test
    void should_returnMd5Digest_when_algorithmIsMd5() {
        // Act
        HashResult result = hashService.hash("MD5", "hello");

        // Assert
        assertThat(result.getAlgorithm()).isEqualTo("MD5");
        assertThat(result.getContent()).isEqualTo("hello");
        assertThat(result.getDigest()).isEqualTo("5d41402abc4b2a76b9719d911017c592");
    }

    @Test
    void should_returnSha256Digest_when_algorithmIsSha256() {
        // Act
        HashResult result = hashService.hash("SHA256", "hello");

        // Assert
        assertThat(result.getAlgorithm()).isEqualTo("SHA256");
        assertThat(result.getDigest()).isEqualTo("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824");
    }

    @Test
    void should_throwBusinessException_when_contentIsEmpty() {
        // Act & Assert
        assertThatThrownBy(() -> hashService.hash("MD5", ""))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getErrorCode()).isEqualTo("ALG_001");
                });
    }

    @Test
    void should_throwBusinessException_when_algorithmIsUnsupported() {
        // Act & Assert
        assertThatThrownBy(() -> hashService.hash("SHA-512", "hello"))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getErrorCode()).isEqualTo("ALG_002");
                });
    }
}
