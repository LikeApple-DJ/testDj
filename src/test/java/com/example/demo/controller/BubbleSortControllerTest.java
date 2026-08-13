package com.example.demo.controller;

import com.example.demo.dto.SortResponse;
import com.example.demo.service.BubbleSortService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BubbleSortController.class)
class BubbleSortControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BubbleSortService bubbleSortService;

    @Test
    void testSort() throws Exception {
        when(bubbleSortService.bubbleSort(List.of(5, 3, 8, 1, 9)))
            .thenReturn(new SortResponse(List.of(5, 3, 8, 1, 9), List.of(1, 3, 5, 8, 9), 6));

        mockMvc.perform(post("/api/demo/bubble-sort")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"array\": [5, 3, 8, 1, 9]}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.original").isArray())
            .andExpect(jsonPath("$.sorted[0]").value(1))
            .andExpect(jsonPath("$.sorted[4]").value(9))
            .andExpect(jsonPath("$.steps").value(6));
    }
}
