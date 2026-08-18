package com.example.demo.dto;

import java.util.List;

public class WeatherResponse {
    private String city;
    private String updateTime;
    private List<WeatherDay> days;
    private String dressAdvice;
    private String outdoorStrategy;

    public WeatherResponse(String city, String updateTime, List<WeatherDay> days,
                           String dressAdvice, String outdoorStrategy) {
        this.city = city;
        this.updateTime = updateTime;
        this.days = days;
        this.dressAdvice = dressAdvice;
        this.outdoorStrategy = outdoorStrategy;
    }

    public String getCity() { return city; }
    public String getUpdateTime() { return updateTime; }
    public List<WeatherDay> getDays() { return days; }
    public String getDressAdvice() { return dressAdvice; }
    public String getOutdoorStrategy() { return outdoorStrategy; }
}