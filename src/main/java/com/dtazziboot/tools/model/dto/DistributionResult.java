package com.dtazziboot.tools.model.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 维度分布出参（饼图/柱状图）。
 */
public class DistributionResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分布项列表。 */
    private List<DistributionItem> items;

    public List<DistributionItem> getItems() {
        return items;
    }

    public void setItems(List<DistributionItem> items) {
        this.items = items;
    }
}