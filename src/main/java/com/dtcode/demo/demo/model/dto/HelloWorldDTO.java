package com.dtcode.demo.demo.model.dto;

/**
 * HelloWorld 响应数据
 *
 * @author DTCoder
 */
public class HelloWorldDTO {

    private String result;
    private String timestamp;

    public HelloWorldDTO() {
    }

    public HelloWorldDTO(String result, String timestamp) {
        this.result = result;
        this.timestamp = timestamp;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "HelloWorldDTO{result='" + result + "', timestamp='" + timestamp + "'}";
    }
}
