package com.example.demo.dto;

public class WeatherDay {
    private String date;
    private String weekDay;
    private String weather;
    private int highTemp;
    private int lowTemp;
    private String rating;
    private String suggestion;

    public WeatherDay(String date, String weekDay, String weather,
                      int highTemp, int lowTemp, String rating, String suggestion) {
        this.date = date;
        this.weekDay = weekDay;
        this.weather = weather;
        this.highTemp = highTemp;
        this.lowTemp = lowTemp;
        this.rating = rating;
        this.suggestion = suggestion;
    }

    public String getDate() { return date; }
    public String getWeekDay() { return weekDay; }
    public String getWeather() { return weather; }
    public int getHighTemp() { return highTemp; }
    public int getLowTemp() { return lowTemp; }
    public String getRating() { return rating; }
    public String getSuggestion() { return suggestion; }
}