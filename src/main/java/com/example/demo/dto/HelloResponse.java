package com.example.demo.dto;

import java.time.LocalDateTime;

public class HelloResponse {
    private String message;
    private String timestamp;

    public HelloResponse() {}

    public HelloResponse(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now().toString();
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
