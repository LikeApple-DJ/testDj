package com.dt.example.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ExportService 测试")
class ExportServiceTest {

    private final ExportService service = new ExportService(new ObjectMapper());
    private final List<Map<String, Object>> rows =
        List.of(Map.of("key", "greeting", "value", "Hello, World!"));

    @Test
    @DisplayName("JSON 导出应返回 JSON 字节")
    void json() throws Exception {
        ExportService.ExportFile file = service.export("json", "helloworld", rows);
        assertThat(file.filename()).isEqualTo("helloworld.json");
        assertThat(new String(file.content(), StandardCharsets.UTF_8))
            .contains("\"key\"");
    }

    @Test
    @DisplayName("CSV 导出应包含表头")
    void csv() throws Exception {
        ExportService.ExportFile file = service.export("csv", "helloworld", rows);
        assertThat(file.filename()).isEqualTo("helloworld.csv");
        assertThat(new String(file.content(), StandardCharsets.UTF_8))
            .contains("key,value");
    }

    @Test
    @DisplayName("XLSX 导出应产生非空内容")
    void xlsx() throws Exception {
        ExportService.ExportFile file = service.export("xlsx", "helloworld", rows);
        assertThat(file.filename()).isEqualTo("helloworld.xlsx");
        assertThat(file.content()).isNotEmpty();
    }

    @Test
    @DisplayName("不支持的格式应抛异常")
    void unsupported() {
        assertThatThrownBy(() -> service.export("pdf", "helloworld", rows))
            .isInstanceOf(IllegalArgumentException.class);
    }
}