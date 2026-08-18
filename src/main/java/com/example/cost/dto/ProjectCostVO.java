package com.example.cost.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProjectCostVO {
    private Summary summary;
    private List<Item> items;

    @Data
    public static class Summary {
        private BigDecimal totalBudget;
        private BigDecimal totalActual;
        private BigDecimal totalRatio;
        private BigDecimal totalOverspend;
    }

    @Data
    public static class Item {
        private String projectName;
        private BigDecimal budget;
        private BigDecimal actual;
        private BigDecimal ratio;
        private BigDecimal overspend;
        private String department;
        private String businessLine;
    }
}