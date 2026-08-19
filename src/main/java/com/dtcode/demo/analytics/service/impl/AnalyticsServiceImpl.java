package com.dtcode.demo.analytics.service.impl;

import com.dtcode.demo.analytics.dao.entity.ApiCallLogDO;
import com.dtcode.demo.analytics.dao.mapper.ApiCallLogMapper;
import com.dtcode.demo.analytics.model.dto.CallSummaryDTO;
import com.dtcode.demo.analytics.model.dto.DistributionDTO;
import com.dtcode.demo.analytics.model.dto.TrendDTO;
import com.dtcode.demo.analytics.service.AnalyticsService;
import com.dtcode.demo.common.constant.ResponseStatusEnum;
import com.dtcode.demo.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调用分析服务实现
 *
 * @author DTCoder
 */
@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsServiceImpl.class);

    private static final List<String> VALID_DIMENSIONS = Arrays.asList("caller_type", "caller_level", "caller_dept");
    private static final List<String> VALID_GRANULARITIES = Arrays.asList("hour", "day", "week", "month");
    private static final int DEFAULT_DATE_RANGE_DAYS = 7;

    private final ApiCallLogMapper apiCallLogMapper;

    public AnalyticsServiceImpl(ApiCallLogMapper apiCallLogMapper) {
        this.apiCallLogMapper = apiCallLogMapper;
    }

    @Override
    @Async("asyncExecutor")
    public void recordCallAsync(String apiName, String requestParams, long durationMs,
                                String callerId, String callerName, String callerType,
                                String callerLevel, String callerDept) {
        try {
            ApiCallLogDO logDO = new ApiCallLogDO();
            logDO.setApiName(apiName);
            logDO.setCallerId(callerId != null ? callerId : "UNKNOWN");
            logDO.setCallerName(callerName != null ? callerName : "");
            logDO.setCallerType(callerType != null ? callerType : "");
            logDO.setCallerLevel(callerLevel != null ? callerLevel : "");
            logDO.setCallerDept(callerDept != null ? callerDept : "");
            logDO.setRequestParams(requestParams);
            logDO.setResponseStatus(ResponseStatusEnum.SUCCESS.getCode());
            logDO.setCallDurationMs((int) durationMs);
            apiCallLogMapper.insert(logDO);
        } catch (Exception e) {
            logger.warn("异步埋点记录失败: apiName={}, error={}", apiName, e.getMessage());
        }
    }

    /**
     * 查询调用汇总统计
     *
     * @param dimension 统计维度
     * @param apiName   接口名称
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 汇总统计结果
     */
    @Override
    public CallSummaryDTO getSummary(String dimension, String apiName, String startDate, String endDate) {
        validateDimension(dimension);
        Date[] dates = parseDateRange(startDate, endDate);

        List<Map<String, Object>> rows = apiCallLogMapper.selectSummary(dimension, apiName, dates[0], dates[1]);
        List<CallSummaryDTO.SummaryItemDTO> items = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            CallSummaryDTO.SummaryItemDTO item = new CallSummaryDTO.SummaryItemDTO();
            item.setGroupKey(String.valueOf(row.get("groupKey")));
            item.setCallCount(toInt(row.get("callCount")));
            item.setUniqueCallers(toInt(row.get("uniqueCallers")));
            items.add(item);
        }

        CallSummaryDTO dto = new CallSummaryDTO();
        dto.setDimension(dimension);
        dto.setItems(items);
        return dto;
    }

    /**
     * 查询调用趋势
     *
     * @param apiName     接口名称
     * @param granularity 时间粒度
     * @param startDate   开始日期
     * @param endDate     结束日期
     * @return 趋势数据
     */
    @Override
    public TrendDTO getTrend(String apiName, String granularity, String startDate, String endDate) {
        if (granularity == null || granularity.trim().isEmpty()) {
            granularity = "day";
        }
        if (!VALID_GRANULARITIES.contains(granularity)) {
            throw new BusinessException("ANALYTICS_003", "粒度参数无效，可选值：hour/day/week/month");
        }

        Date[] dates = parseDateRange(startDate, endDate);
        String dateFormat = toDateFormat(granularity);

        List<Map<String, Object>> rows = apiCallLogMapper.selectTrend(dateFormat, apiName, dates[0], dates[1]);
        List<TrendDTO.TrendPointDTO> points = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            TrendDTO.TrendPointDTO point = new TrendDTO.TrendPointDTO();
            point.setTimeLabel(String.valueOf(row.get("timeLabel")));
            point.setCallCount(toInt(row.get("callCount")));
            points.add(point);
        }

        TrendDTO dto = new TrendDTO();
        dto.setGranularity(granularity);
        dto.setPoints(points);
        return dto;
    }

    /**
     * 查询调用分布
     *
     * @param dimension 统计维度
     * @param apiName   接口名称
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 分布数据
     */
    @Override
    public DistributionDTO getDistribution(String dimension, String apiName, String startDate, String endDate) {
        validateDimension(dimension);
        Date[] dates = parseDateRange(startDate, endDate);

        List<Map<String, Object>> rows = apiCallLogMapper.selectDistribution(dimension, apiName, dates[0], dates[1]);
        Long total = apiCallLogMapper.selectTotalCount(apiName, dates[0], dates[1]);
        if (total == null || total == 0) {
            total = 1L;
        }

        List<DistributionDTO.DistributionItemDTO> items = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            DistributionDTO.DistributionItemDTO item = new DistributionDTO.DistributionItemDTO();
            item.setGroupKey(String.valueOf(row.get("groupKey")));
            item.setCallCount(toInt(row.get("callCount")));
            double pct = Math.round((double) item.getCallCount() / total * 1000.0) / 10.0;
            item.setPercentage(pct);
            items.add(item);
        }

        DistributionDTO dto = new DistributionDTO();
        dto.setDimension(dimension);
        dto.setItems(items);
        return dto;
    }

    private void validateDimension(String dimension) {
        if (dimension == null || !VALID_DIMENSIONS.contains(dimension)) {
            throw new BusinessException("ANALYTICS_001", "维度参数无效，可选值：caller_type/caller_level/caller_dept");
        }
    }

    private Date[] parseDateRange(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start;
            Date end;
            if (endDate == null || endDate.trim().isEmpty()) {
                end = Date.from(Instant.now());
            } else {
                end = sdf.parse(endDate);
            }
            if (startDate == null || startDate.trim().isEmpty()) {
                LocalDate endDateLocal = end.toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                start = java.sql.Date.valueOf(
                        endDateLocal.minusDays(DEFAULT_DATE_RANGE_DAYS));
            } else {
                start = sdf.parse(startDate);
            }
            return new Date[]{start, end};
        } catch (ParseException e) {
            throw new BusinessException("ANALYTICS_002",
                    "日期格式无效，请使用 yyyy-MM-dd 格式", e);
        }
    }

    private String toDateFormat(String granularity) {
        Map<String, String> formatMap = new HashMap<>();
        formatMap.put("hour", "%Y-%m-%d %H:00");
        formatMap.put("day", "%Y-%m-%d");
        formatMap.put("week", "%Y-%u");
        formatMap.put("month", "%Y-%m");
        return formatMap.getOrDefault(granularity, "%Y-%m-%d");
    }

    private int toInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }
}
