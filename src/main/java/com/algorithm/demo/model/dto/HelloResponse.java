package com.algorithm.demo.model.dto;

import java.time.LocalDateTime;

/**
 * HelloWorld 响应数据
 *
 * @author DTCoder
 */
public class HelloResponse {

    /** HelloWorld 消息 */
    private String message;

    /** 执行时间戳 */
    private String timestamp;

    public HelloResponse() {
    }

    public HelloResponse(String message, String timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
