package com.dtazziboot.tools.model.dto;

import java.io.Serializable;

/**
 * 调用趋势点（折线图）。
 */
public class TrendPoint implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 时间点 yyyy-MM-dd。 */
    private String time;

    /** 调用次数。 */
    private Long count;

    public TrendPoint() {
    }

    public TrendPoint(String time, Long count) {
        this.time = time;
        this.count = count;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}