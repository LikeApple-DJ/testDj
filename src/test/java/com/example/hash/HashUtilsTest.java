package com.example.hash;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * HashUtils 单元测试类
 *
 * @author AiWork
 * @date 2026/10/21
 */
class HashUtilsTest {

    // ==================== md5 测试 ====================

    @Nested
    @DisplayName("MD5 哈希测试")
    class Md5Tests {

        @Test
        @DisplayName("正常字符串 → 返回正确MD5十六进制")
        void should_returnCorrectMd5Hash_when_normalString() {
            String result = HashUtils.md5("hello world");

            assertThat(result).isEqualTo("5eb63bbbe01eeed093cb22bb8f5acdc3");
        }

        @Test
        @DisplayName("空字符串 → 返回正确的MD5值")
        void should_returnMd5Hash_when_emptyString() {
            String result = HashUtils.md5("");

            assertThat(result).isEqualTo("d41d8cd98f00b204e9800998ecf8427e");
        }

        @Test
        @DisplayName("中文输入 → 返回正确的MD5值")
        void should_returnMd5Hash_when_chineseInput() {
            String result = HashUtils.md5("你好世界");

            assertThat(result).isEqualTo("a78f4501e95bae1e4e49a5ef2a969026");
        }

        @Test
        @DisplayName("null输入 → 抛出IllegalArgumentException")
        void should_throwException_when_inputIsNull() {
            assertThatThrownBy(() -> HashUtils.md5(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("input must not be null");
        }
    }

    // ==================== sha1 测试 ====================

    @Nested
    @DisplayName("SHA-1 哈希测试")
    class Sha1Tests {

        @Test
        @DisplayName("正常字符串 → 返回正确SHA-1十六进制")
        void should_returnCorrectSha1Hash_when_normalString() {
            String result = HashUtils.sha1("hello world");

            assertThat(result).isEqualTo("2aae6c35c94fcfb415dbe95f408b9ce91ee846ed");
        }

        @Test
        @DisplayName("空字符串 → 返回正确的SHA-1值")
        void should_returnSha1Hash_when_emptyString() {
            String result = HashUtils.sha1("");

            assertThat(result).isEqualTo("da39a3ee5e6b4b0d3255bfef95601890afd80709");
        }

        @Test
        @DisplayName("null输入 → 抛出IllegalArgumentException")
        void should_throwException_when_inputIsNull() {
            assertThatThrownBy(() -> HashUtils.sha1(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("input must not be null");
        }
    }

    // ==================== sha256 测试 ====================

    @Nested
    @DisplayName("SHA-256 哈希测试")
    class Sha256Tests {

        @Test
        @DisplayName("正常字符串 → 返回正确SHA-256十六进制")
        void should_returnCorrectSha256Hash_when_normalString() {
            String result = HashUtils.sha256("hello world");

            assertThat(result).isEqualTo("b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9");
        }

        @Test
        @DisplayName("空字符串 → 返回正确的SHA-256值")
        void should_returnSha256Hash_when_emptyString() {
            String result = HashUtils.sha256("");

            assertThat(result).isEqualTo("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
        }

        @Test
        @DisplayName("null输入 → 抛出IllegalArgumentException")
        void should_throwException_when_inputIsNull() {
            assertThatThrownBy(() -> HashUtils.sha256(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("input must not be null");
        }
    }

    // ==================== hash 通用方法测试 ====================

    @Nested
    @DisplayName("通用哈希方法测试")
    class HashTests {

        @Test
        @DisplayName("指定算法MD5 → 结果与直接调用一致")
        void should_returnMd5Hash_when_algorithmIsMd5() {
            String result = HashUtils.hash("test", HashUtils.ALGORITHM_MD5);

            assertThat(result).isEqualTo(HashUtils.md5("test"));
        }

        @Test
        @DisplayName("指定算法SHA-1 → 结果与直接调用一致")
        void should_returnSha1Hash_when_algorithmIsSha1() {
            String result = HashUtils.hash("test", HashUtils.ALGORITHM_SHA1);

            assertThat(result).isEqualTo(HashUtils.sha1("test"));
        }

        @Test
        @DisplayName("指定算法SHA-256 → 结果与直接调用一致")
        void should_returnSha256Hash_when_algorithmIsSha256() {
            String result = HashUtils.hash("test", HashUtils.ALGORITHM_SHA256);

            assertThat(result).isEqualTo(HashUtils.sha256("test"));
        }

        @ParameterizedTest
        @CsvSource({
                "hello, sha256, test value 1",
                "world, md5, test value 2",
                "foo, sha1, test value 3"
        })
        @DisplayName("不同输入+算法组合 → 返回非空且长度正确")
        void should_returnValidHash_when_variousInputs(String input, String algorithm, String caseDesc) {
            String result = HashUtils.hash(input, algorithm);

            assertThat(result).isNotBlank();
            assertThat(result).matches("^[a-f0-9]+$");
        }

        @Test
        @DisplayName("null输入 → 抛出IllegalArgumentException")
        void should_throwException_when_inputIsNull() {
            assertThatThrownBy(() -> HashUtils.hash(null, "MD5"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("input must not be null");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("空或null算法 → 抛出IllegalArgumentException")
        void should_throwException_when_algorithmIsNullOrEmpty(String algorithm) {
            assertThatThrownBy(() -> HashUtils.hash("test", algorithm))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("algorithm must not be null or empty");
        }

        @Test
        @DisplayName("不支持的算法 → 抛出IllegalArgumentException")
        void should_throwException_when_algorithmIsUnsupported() {
            assertThatThrownBy(() -> HashUtils.hash("test", "UNKNOWN_ALGO"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Unsupported algorithm");
        }
    }

    // ==================== 一致性验证 ====================

    @Test
    @DisplayName("相同输入多次哈希 → 结果一致（幂等性）")
    void should_produceSameResult_when_sameInput() {
        String input = "consistent test";
        String first = HashUtils.md5(input);
        String second = HashUtils.md5(input);
        String third = HashUtils.md5(input);

        assertThat(first).isEqualTo(second).isEqualTo(third);
    }

    @Test
    @DisplayName("不同算法对相同输入 → 结果不同")
    void should_produceDifferentResults_when_differentAlgorithms() {
        String input = "compare algorithms";

        String md5Result = HashUtils.md5(input);
        String sha1Result = HashUtils.sha1(input);
        String sha256Result = HashUtils.sha256(input);

        assertThat(md5Result).isNotEqualTo(sha1Result);
        assertThat(sha1Result).isNotEqualTo(sha256Result);
        assertThat(md5Result).isNotEqualTo(sha256Result);
    }

    @Test
    @DisplayName("输出长度验证 → MD5=32, SHA-1=40, SHA-256=64")
    void should_haveCorrectOutputLength_when_hashing() {
        String input = "length test";

        assertThat(HashUtils.md5(input)).hasSize(32);
        assertThat(HashUtils.sha1(input)).hasSize(40);
        assertThat(HashUtils.sha256(input)).hasSize(64);
    }
}