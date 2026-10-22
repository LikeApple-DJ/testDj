package com.dtazziboot.tools.model.dto;

import java.io.Serializable;

/**
 * 维度分布项（饼图/柱状图）。
 */
public class DistributionItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 维度取值（如 P6 / LEVEL2 / 部门名）。 */
    private String key;

    /** 调用次数。 */
    private Long count;

    public DistributionItem() {
    }

    public DistributionItem(String key, Long count) {
        this.key = key;
        this.count = count;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}