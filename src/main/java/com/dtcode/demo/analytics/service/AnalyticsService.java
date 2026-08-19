package com.dtcode.demo.analytics.service;

/**
 * 调用分析服务接口
 *
 * @author DTCoder
 */
public interface AnalyticsService {

    /**
     * 异步记录接口调用埋点
     *
     * @param apiName      接口名称
     * @param requestParams 请求参数快照
     * @param durationMs   调用耗时（毫秒）
     */
    void recordCallAsync(String apiName, String requestParams, long durationMs);
}
