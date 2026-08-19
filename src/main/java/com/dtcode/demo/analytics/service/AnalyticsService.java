package com.dtcode.demo.analytics.service;

import com.dtcode.demo.analytics.model.dto.CallSummaryDTO;
import com.dtcode.demo.analytics.model.dto.DistributionDTO;
import com.dtcode.demo.analytics.model.dto.TrendDTO;

/**
 * 调用分析服务接口
 *
 * @author DTCoder
 */
public interface AnalyticsService {

    /**
     * 异步记录接口调用埋点
     *
     * @param apiName       接口名称
     * @param requestParams 请求参数快照
     * @param durationMs    调用耗时（毫秒）
     * @param callerId      调用人ID
     * @param callerName    调用人姓名
     * @param callerType    调用人类型
     * @param callerLevel   调用人层级
     * @param callerDept    调用人部门
     */
    void recordCallAsync(String apiName, String requestParams, long durationMs,
                         String callerId, String callerName, String callerType,
                         String callerLevel, String callerDept);

    /**
     * 查询调用汇总统计
     *
     * @param dimension 统计维度
     * @param apiName   接口名称
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 汇总统计结果
     */
    CallSummaryDTO getSummary(String dimension, String apiName, String startDate, String endDate);

    /**
     * 查询调用趋势
     *
     * @param apiName     接口名称
     * @param granularity 时间粒度
     * @param startDate   开始日期
     * @param endDate     结束日期
     * @return 趋势数据
     */
    TrendDTO getTrend(String apiName, String granularity, String startDate, String endDate);

    /**
     * 查询调用分布
     *
     * @param dimension 统计维度
     * @param apiName   接口名称
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 分布数据
     */
    DistributionDTO getDistribution(String dimension, String apiName, String startDate, String endDate);
}
