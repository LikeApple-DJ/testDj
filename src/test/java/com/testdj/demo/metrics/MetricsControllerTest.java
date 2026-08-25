package com.testdj.demo.metrics;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MetricsController.class)
class MetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MetricService metricService;

    @Test
    void shouldReturnReportByUserType() throws Exception {
        List<ReportItem> items = List.of(
                new ReportItem("正式员工", 10L),
                new ReportItem("实习生", 5L));
        when(metricService.report(any(Dimension.class), any(Instant.class), any(Instant.class)))
                .thenReturn(items);

        mockMvc.perform(get("/api/v1/demo/metrics/report")
                        .param("dimension", "USER_TYPE")
                        .param("startDate", "2026-01-01T00:00:00Z")
                        .param("endDate", "2026-12-31T23:59:59Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].dimension").value("正式员工"))
                .andExpect(jsonPath("$.data[0].count").value(10))
                .andExpect(jsonPath("$.data[1].dimension").value("实习生"))
                .andExpect(jsonPath("$.data[1].count").value(5));
    }
}