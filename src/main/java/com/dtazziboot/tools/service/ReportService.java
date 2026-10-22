package com.dtazziboot.tools.service;

import com.dtazziboot.tools.model.dto.DistributionItem;
import com.dtazziboot.tools.model.dto.ReportQuery;
import com.dtazziboot.tools.model.dto.TrendPoint;

import java.util.List;

/**
 * 调用情况报表服务。
 */
public interface ReportService {

    /**
     * 调用趋势聚合（折线图）。
     */
    List<TrendPoint> trend(ReportQuery query);

    /**
     * 维度分布聚合（饼图/柱状图）。
     */
    List<DistributionItem> distribution(ReportQuery query);
}