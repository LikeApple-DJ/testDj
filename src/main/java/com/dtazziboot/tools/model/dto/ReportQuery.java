package com.dtazziboot.tools.model.dto;

import java.io.Serializable;

/**
 * 报表查询入参。
 */
public class ReportQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 统计维度：TYPE / LEVEL / DEPT。 */
    private String dimension;

    /** 开始时间 yyyy-MM-dd。 */
    private String startTime;

    /** 结束时间 yyyy-MM-dd。 */
    private String endTime;

    /** 接口编码过滤。 */
    private String apiCode;

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getApiCode() {
        return apiCode;
    }

    public void setApiCode(String apiCode) {
        this.apiCode = apiCode;
    }
}