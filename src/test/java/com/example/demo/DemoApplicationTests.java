package com.example.demo;
import com.example.demo.model.TrackingRecord;
import com.example.demo.model.dto.AuthResponse;
import com.example.demo.repository.TrackingRecordRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
class DemoApplicationTests {
    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private TrackingRecordRepository trackingRepo;
    @Autowired private ObjectMapper objectMapper;
    private String token;
    @BeforeEach
    void setUp() {
        trackingRepo.deleteAll();
        userRepository.deleteAll();
    }
    @Test
    void fullIntegrationFlow() throws Exception {
        String registerJson = objectMapper.writeValueAsString(Map.of(
            "username", "testuser", "password", "pass123",
            "personType", "技术岗", "personLevel", "高级", "personDept", "研发部"));
        String registerResp = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON).content(registerJson))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        AuthResponse auth = objectMapper.readValue(registerResp, AuthResponse.class);
        token = auth.getToken();
        assertNotNull(token);
        String loginJson = objectMapper.writeValueAsString(Map.of("username", "testuser", "password", "pass123"));
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/helloworld").header("Authorization", "Bearer " + token).param("name", "Alice"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.result").value("Hello, Alice!"));
        String hashJson = objectMapper.writeValueAsString(Map.of("input", "test"));
        mockMvc.perform(post("/api/hash").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(hashJson))
                .andExpect(status().isOk()).andExpect(jsonPath("$.algorithm").value("SHA-256"))
                .andExpect(jsonPath("$.hash").isNotEmpty());
        String sortJson = objectMapper.writeValueAsString(Map.of("array", List.of(5, 3, 8, 1, 2)));
        mockMvc.perform(post("/api/bubblesort").header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(sortJson))
                .andExpect(status().isOk()).andExpect(jsonPath("$.sorted[0]").value(1))
                .andExpect(jsonPath("$.sorted[4]").value(8));
        List<TrackingRecord> records = trackingRepo.findAll();
        assertTrue(records.size() >= 3, "Should have at least 3 tracking records");
        mockMvc.perform(get("/api/tracking/report").header("Authorization", "Bearer " + token)
                .param("dimension", "personType"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].label").value("技术岗"));
        mockMvc.perform(get("/api/export").header("Authorization", "Bearer " + token)
                .param("type", "helloworld"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=helloworld_export.xlsx"));
    }
}