package com.dtcode.demo.analytics.model.dto;

import java.util.List;

/**
 * 调用趋势响应
 *
 * @author DTCoder
 */
public class TrendDTO {

    private String granularity;
    private List<TrendPointDTO> points;

    public String getGranularity() {
        return granularity;
    }

    public void setGranularity(String granularity) {
        this.granularity = granularity;
    }

    public List<TrendPointDTO> getPoints() {
        return points;
    }

    public void setPoints(List<TrendPointDTO> points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "TrendDTO{granularity='" + granularity + "', points=" + points + "}";
    }

    /**
     * 趋势数据点
     */
    public static class TrendPointDTO {
        private String timeLabel;
        private Integer callCount;

        public TrendPointDTO() {
        }

        public TrendPointDTO(String timeLabel, Integer callCount) {
            this.timeLabel = timeLabel;
            this.callCount = callCount;
        }

        public String getTimeLabel() {
            return timeLabel;
        }

        public void setTimeLabel(String timeLabel) {
            this.timeLabel = timeLabel;
        }

        public Integer getCallCount() {
            return callCount;
        }

        public void setCallCount(Integer callCount) {
            this.callCount = callCount;
        }

        @Override
        public String toString() {
            return "TrendPointDTO{timeLabel='" + timeLabel + "', callCount=" + callCount + "}";
        }
    }
}
