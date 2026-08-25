package com.testdj.demo.hash;

import com.testdj.demo.common.ErrorCode;
import com.testdj.demo.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HashServiceTest {

    private final HashService hashService = new HashService();

    @Test
    void shouldReturnSha256ByDefault() {
        HashRequest request = new HashRequest(null, "hello");
        HashResponse response = hashService.hash(request);
        assertEquals("SHA-256", response.algorithm());
        assertEquals("hello", response.original());
        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", response.hash());
    }

    @Test
    void shouldReturnMd5WhenRequested() {
        HashRequest request = new HashRequest("MD5", "hello");
        HashResponse response = hashService.hash(request);
        assertEquals("MD5", response.algorithm());
        assertEquals("5d41402abc4b2a76b9719d911017c592", response.hash());
    }

    @Test
    void shouldThrowWhenContentIsEmpty() {
        HashRequest request = new HashRequest("SHA-256", "");
        BusinessException ex = assertThrows(BusinessException.class, () -> hashService.hash(request));
        assertEquals(ErrorCode.HASH_CONTENT_EMPTY, ex.getCode());
        assertTrue(ex.getMessage().contains("content must not be empty"));
    }

    @Test
    void shouldThrowWhenContentIsNull() {
        HashRequest request = new HashRequest("SHA-256", null);
        BusinessException ex = assertThrows(BusinessException.class, () -> hashService.hash(request));
        assertEquals(ErrorCode.HASH_CONTENT_EMPTY, ex.getCode());
        assertTrue(ex.getMessage().contains("content must not be empty"));
    }

    @Test
    void shouldThrowWhenAlgorithmUnsupported() {
        HashRequest request = new HashRequest("SM3", "hello");
        BusinessException ex = assertThrows(BusinessException.class, () -> hashService.hash(request));
        assertEquals(ErrorCode.HASH_UNSUPPORTED_ALGORITHM, ex.getCode());
        assertTrue(ex.getMessage().contains("unsupported algorithm"));
    }
}
