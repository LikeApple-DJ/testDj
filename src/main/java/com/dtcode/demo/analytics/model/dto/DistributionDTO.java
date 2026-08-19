package com.dtcode.demo.analytics.model.dto;

import java.util.List;

/**
 * 调用分布响应
 *
 * @author DTCoder
 */
public class DistributionDTO {

    private String dimension;
    private List<DistributionItemDTO> items;

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public List<DistributionItemDTO> getItems() {
        return items;
    }

    public void setItems(List<DistributionItemDTO> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "DistributionDTO{dimension='" + dimension + "', items=" + items + "}";
    }

    /**
     * 分布数据项
     */
    public static class DistributionItemDTO {
        private String groupKey;
        private Integer callCount;
        private Double percentage;

        public String getGroupKey() {
            return groupKey;
        }

        public void setGroupKey(String groupKey) {
            this.groupKey = groupKey;
        }

        public Integer getCallCount() {
            return callCount;
        }

        public void setCallCount(Integer callCount) {
            this.callCount = callCount;
        }

        public Double getPercentage() {
            return percentage;
        }

        public void setPercentage(Double percentage) {
            this.percentage = percentage;
        }

        @Override
        public String toString() {
            return "DistributionItemDTO{groupKey='" + groupKey + "', callCount=" + callCount
                    + ", percentage=" + percentage + "}";
        }
    }
}
