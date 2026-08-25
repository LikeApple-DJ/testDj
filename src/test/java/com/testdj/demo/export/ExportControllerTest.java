package com.testdj.demo.export;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExportController.class)
class ExportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExportService exportService;

    @Test
    void shouldExportCsv() throws Exception {
        String csvContent = "Hello, World!\n";
        when(exportService.export(any(ExportRequest.class))).thenReturn(csvContent.getBytes());

        mockMvc.perform(post("/api/v1/demo/export")
                        .contentType("application/json")
                        .content("{\"tab\":\"hello\",\"format\":\"csv\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"demo-export.csv\""))
                .andExpect(header().string("Content-Type", "application/octet-stream"));
    }

    @Test
    void shouldExportExcel() throws Exception {
        byte[] excelBytes = new byte[]{0, 1, 2};
        when(exportService.export(any(ExportRequest.class))).thenReturn(excelBytes);

        mockMvc.perform(post("/api/v1/demo/export")
                        .contentType("application/json")
                        .content("{\"tab\":\"hash\",\"format\":\"excel\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"demo-export.xlsx\""))
                .andExpect(header().string("Content-Type", "application/octet-stream"));
    }
}
