package com.example.demo.controller;

import com.example.demo.service.HelloService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HelloService helloService;

    @Test
    void testHello() throws Exception {
        when(helloService.sayHello("World"))
            .thenReturn(new com.example.demo.dto.HelloResponse("Hello, World!"));

        mockMvc.perform(post("/api/demo/hello")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"World\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Hello, World!"))
            .andExpect(jsonPath("$.timestamp").exists());
    }
}
