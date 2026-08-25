package com.example.demo.controller;
import com.example.demo.service.TrackingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Set;
@RestController
@RequestMapping("/api/tracking")
public class TrackingController {
    private final TrackingService trackingService;
    private static final Set<String> VALID_DIMENSIONS = Set.of("personType", "personLevel", "personDept");
    public TrackingController(TrackingService trackingService) { this.trackingService = trackingService; }
    @GetMapping("/report")
    public ResponseEntity<?> report(@RequestParam String dimension) {
        if (!VALID_DIMENSIONS.contains(dimension)) {
            return ResponseEntity.badRequest().body(Map.of("code", "REPORT_001", "message", "不支持的维度参数"));
        }
        return ResponseEntity.ok(trackingService.getReport(dimension));
    }
}