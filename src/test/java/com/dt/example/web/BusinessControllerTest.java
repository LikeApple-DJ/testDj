package com.dt.example.web;

import com.dt.example.algorithm.BubbleSortService;
import com.dt.example.algorithm.HashService;
import com.dt.example.metrics.MetricsCollector;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BusinessController.class)
@Import({HashService.class, BubbleSortService.class, MetricsCollector.class})
@DisplayName("BusinessController 测试")
class BusinessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/helloWorld 应返回固定问候语")
    void helloWorld() throws Exception {
        mockMvc.perform(get("/api/helloWorld"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data").value("Hello, World!"));
    }

    @Test
    @DisplayName("POST /api/hash 应返回 MD5")
    void hash() throws Exception {
        String body = objectMapper.writeValueAsString(
            new BusinessController.HashRequest("abc"));
        mockMvc.perform(post("/api/hash")
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.algorithm").value("MD5"))
            .andExpect(jsonPath("$.data.hash")
                .value("900150983cd24fb0d6963f7d28e17f72"));
    }

    @Test
    @DisplayName("POST /api/sort/bubble 应返回升序数组")
    void bubbleSort() throws Exception {
        String body = "{\"numbers\":[5,3,8,1]}";
        mockMvc.perform(post("/api/sort/bubble")
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.sorted[0]").value(1))
            .andExpect(jsonPath("$.data.sorted[3]").value(8));
    }
}