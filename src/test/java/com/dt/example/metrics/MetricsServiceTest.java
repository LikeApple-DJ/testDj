package com.dt.example.metrics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MetricsService 测试")
class MetricsServiceTest {

    private final MetricsCollector collector = new MetricsCollector();
    private final PersonDictionary dictionary = new PersonDictionary();
    private final MetricsService service = new MetricsService(collector, dictionary);

    @Test
    @DisplayName("pie 应按部门聚合并保留类别")
    void pie_byDepartment() {
        collector.record("u001", "/api/helloWorld");
        collector.record("u002", "/api/hash");
        collector.record("u003", "/api/sort/bubble");

        MetricsQueryResponse resp = service.query("personDepartment", "pie");

        assertThat(resp.total()).isEqualTo(3);
        assertThat(resp.categories()).contains("研发部", "产品部");
        assertThat(resp.series()).hasSize(1);
    }

    @Test
    @DisplayName("bar 应按接口拆分系列")
    void bar_byType() {
        collector.record("u001", "/api/helloWorld");
        collector.record("u003", "/api/helloWorld");

        MetricsQueryResponse resp = service.query("personType", "bar");

        assertThat(resp.series()).hasSize(3);
        assertThat(resp.series().get(0).name()).isEqualTo("helloworld");
    }

    @Test
    @DisplayName("line 应按时间桶聚合")
    void line_byTime() {
        collector.record("u001", "/api/helloWorld");
        MetricsQueryResponse resp = service.query("time", "line");
        assertThat(resp.series()).hasSize(1);
        assertThat(resp.total()).isEqualTo(1);
    }

    @Test
    @DisplayName("未知维度应抛 IllegalArgumentException")
    void unknown_dimension() {
        assertThatThrownBy(() -> service.query("badDim", "pie"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}