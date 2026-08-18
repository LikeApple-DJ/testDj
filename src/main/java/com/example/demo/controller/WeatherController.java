package com.example.demo.controller;

import com.example.demo.common.ApiResult;
import com.example.demo.dto.WeatherResponse;
import com.example.demo.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    public ResponseEntity<ApiResult<WeatherResponse>> getWeather(
            @RequestParam(defaultValue = "hangzhou") String city) {
        try {
            WeatherResponse data = weatherService.getWeather(city);
            return ResponseEntity.ok(ApiResult.success(data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResult.error(400, e.getMessage()));
        }
    }
}