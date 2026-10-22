package com.dtazziboot.tools.model.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 调用趋势出参（折线图）。
 */
public class TrendResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 趋势点列表。 */
    private List<TrendPoint> points;

    public List<TrendPoint> getPoints() {
        return points;
    }

    public void setPoints(List<TrendPoint> points) {
        this.points = points;
    }
}