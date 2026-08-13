package com.example.demo.controller;

import com.example.demo.dto.HashResponse;
import com.example.demo.service.HashService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HashController.class)
class HashControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HashService hashService;

    @Test
    void testHash() throws Exception {
        when(hashService.computeHash("hello", "SHA-256"))
            .thenReturn(new HashResponse("hello", "SHA-256",
                "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"));

        mockMvc.perform(post("/api/demo/hash")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"input\": \"hello\", \"algorithm\": \"SHA-256\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.input").value("hello"))
            .andExpect(jsonPath("$.algorithm").value("SHA-256"))
            .andExpect(jsonPath("$.hash").value("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"));
    }
}
