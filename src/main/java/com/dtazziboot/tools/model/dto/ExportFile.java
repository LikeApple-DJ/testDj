package com.dtazziboot.tools.model.dto;

import java.io.Serializable;

/**
 * 导出文件载体。
 */
public class ExportFile implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 文件名（含扩展名）。 */
    private String fileName;

    /** 内容类型。 */
    private String contentType;

    /** 文件字节流。 */
    private byte[] data;

    public ExportFile() {
    }

    public ExportFile(String fileName, String contentType, byte[] data) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}