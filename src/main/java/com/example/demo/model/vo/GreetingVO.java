package com.example.demo.model.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 问候语视图对象。
 *
 * @author AiWork
 */
public class GreetingVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 问候语内容 */
    private String message;

    /** 用户名称 */
    private String name;

    /** 生成时间戳 */
    private LocalDateTime timestamp;

    public GreetingVO() {
    }

    public GreetingVO(String message, String name, LocalDateTime timestamp) {
        this.message = message;
        this.name = name;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}