package com.example.demo.controller;

import com.example.demo.common.ApiResult;
import com.example.demo.dto.WeatherResponse;
import com.example.demo.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 天气查询接口控制器。
 */
@RestController
@RequestMapping("/api")
public class WeatherController {

    private static final Logger log = LoggerFactory.getLogger(WeatherController.class);

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * 查询指定城市未来 7 天天气。
     */
    @GetMapping("/weather")
    public ResponseEntity<ApiResult<WeatherResponse>> getWeather(
            @RequestParam(defaultValue = "hangzhou") String city) {
        try {
            WeatherResponse data = weatherService.getWeather(city);
            return ResponseEntity.ok(ApiResult.success(data));
        } catch (IllegalArgumentException e) {
            log.error("天气查询参数非法: city={}", city, e);
            return ResponseEntity.badRequest()
                    .body(ApiResult.error(400, e.getMessage()));
        }
    }
}