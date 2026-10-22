package com.dtazziboot.tools.model.dto;

import java.io.Serializable;

/**
 * helloworld 出参。
 */
public class HelloResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 问候语。 */
    private String message;

    public HelloResult() {
    }

    public HelloResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}