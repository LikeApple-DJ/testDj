package com.dtazziboot.tools.model.dto;

import java.io.Serializable;

/**
 * 导出请求参数。
 */
public class ExportRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 导出类型：helloworld / hash / bubble-sort / report。 */
    private String type;

    /** 导出格式：xlsx / csv，默认 xlsx。 */
    private String format;

    /** 各类型对应参数（三接口入参或报表筛选条件）。 */
    private String params;

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

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}