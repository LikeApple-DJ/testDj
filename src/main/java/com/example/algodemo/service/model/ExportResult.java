package com.example.algodemo.service.model;

/**
 * 导出结果。
 */
public class ExportResult {

    private String filename;
    private String content;
    private String contentType;

    public ExportResult() {
    }

    public ExportResult(String filename, String content, String contentType) {
        this.filename = filename;
        this.content = content;
        this.contentType = contentType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
