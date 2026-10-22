package com.dtazziboot.tools.service.impl;

import com.dtazziboot.tools.common.constant.ErrorCodeConsts;
import com.dtazziboot.tools.common.enums.ReportDimensionEnum;
import com.dtazziboot.tools.common.exception.BizException;
import com.dtazziboot.tools.dao.mapper.InvokeLogMapper;
import com.dtazziboot.tools.model.dto.DistributionItem;
import com.dtazziboot.tools.model.dto.ReportQuery;
import com.dtazziboot.tools.model.dto.TrendPoint;
import com.dtazziboot.tools.service.ReportService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 调用情况报表服务实现：只读聚合，默认近 7 天。
 */
@Service
public class ReportServiceImpl implements ReportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final InvokeLogMapper invokeLogMapper;

    public ReportServiceImpl(InvokeLogMapper invokeLogMapper) {
        this.invokeLogMapper = invokeLogMapper;
    }

    @Override
    public List<TrendPoint> trend(ReportQuery query) {
        TimeRange range = resolveTimeRange(query);
        List<TrendPoint> points = invokeLogMapper.listTrend(range.start(), range.end(), query.getApiCode());
        return points == null ? List.of() : points;
    }

    @Override
    public List<DistributionItem> distribution(ReportQuery query) {
        ReportDimensionEnum dimension = ReportDimensionEnum.fromName(query.getDimension());
        if (dimension == null) {
            throw new BizException(ErrorCodeConsts.ERR_DIMENSION,
                    "unsupported dimension: " + query.getDimension(), "维度仅支持 TYPE / LEVEL / DEPT");
        }
        TimeRange range = resolveTimeRange(query);
        List<DistributionItem> items;
        switch (dimension) {
            case TYPE -> items = invokeLogMapper.listDistributionByType(range.start(), range.end(), query.getApiCode());
            case LEVEL -> items = invokeLogMapper.listDistributionByLevel(range.start(), range.end(), query.getApiCode());
            case DEPT -> items = invokeLogMapper.listDistributionByDept(range.start(), range.end(), query.getApiCode());
            default -> items = List.of();
        }
        return items == null ? List.of() : items;
    }

    private TimeRange resolveTimeRange(ReportQuery query) {
        LocalDate endDate = parseDate(query.getEndTime(), LocalDate.now().plusDays(1));
        LocalDate startDate = parseDate(query.getStartTime(), endDate.minusDays(7));
        return new TimeRange(startDate.atStartOfDay(), endDate.atStartOfDay());
    }

    private LocalDate parseDate(String value, LocalDate fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        try {
            return LocalDate.parse(value.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return fallback;
        }
    }

    private record TimeRange(LocalDateTime start, LocalDateTime end) {
    }
}