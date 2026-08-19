package com.dtcode.demo.analytics.model.dto;

import java.util.List;

/**
 * 调用统计汇总响应
 *
 * @author DTCoder
 */
public class CallSummaryDTO {

    private String dimension;
    private List<SummaryItemDTO> items;

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public List<SummaryItemDTO> getItems() {
        return items;
    }

    public void setItems(List<SummaryItemDTO> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "CallSummaryDTO{dimension='" + dimension + "', items=" + items + "}";
    }

    /**
     * 汇总分组项
     */
    public static class SummaryItemDTO {
        private String groupKey;
        private Integer callCount;
        private Integer uniqueCallers;

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

        public Integer getUniqueCallers() {
            return uniqueCallers;
        }

        public void setUniqueCallers(Integer uniqueCallers) {
            this.uniqueCallers = uniqueCallers;
        }

        @Override
        public String toString() {
            return "SummaryItemDTO{groupKey='" + groupKey + "', callCount=" + callCount
                    + ", uniqueCallers=" + uniqueCallers + "}";
        }
    }
}
