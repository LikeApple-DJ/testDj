package com.example.cost.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardVO {
    private LaborCostBlock laborCost;
    private ProjectCostBlock projectCost;
    private List<TrendItem> trend;
    private List<TopOverspend> topOverspendProjects;

    @Data
    public static class LaborCostBlock {
        private BigDecimal total;
        private List<RoleCost> byRole;
    }

    @Data
    public static class RoleCost {
        private String role;
        private BigDecimal cost;
    }

    @Data
    public static class ProjectCostBlock {
        private BigDecimal totalBudget;
        private BigDecimal totalActual;
        private BigDecimal totalRatio;
    }

    @Data
    public static class TrendItem {
        private String label;
        private BigDecimal laborCost;
        private BigDecimal projectCost;
    }

    @Data
    public static class TopOverspend {
        private String projectName;
        private BigDecimal overspend;
    }
}