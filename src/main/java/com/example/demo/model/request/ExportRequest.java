package com.example.demo.model.request;

import java.util.Map;

/**
 * 导出请求参数。
 */
public class ExportRequest {

    /** 导出类型：hello / hash / sort，必填 */
    private String type;

    /** 导出格式：json（默认）/ csv */
    private String format;

    /** 对应 Tab 的当前展示结果数据，必填 */
    private Map<String, Object> data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}