package com.example.cost.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class LaborCostVO {
    private Summary summary;
    private List<Breakdown> breakdown;

    @Data
    public static class Summary {
        private BigDecimal totalLaborCost;
        private BigDecimal avgCostPerPerson;
        private int headcount;
    }

    @Data
    public static class Breakdown {
        private String role;
        private BigDecimal cost;
        private int headcount;
        private BigDecimal ratio;
    }
}