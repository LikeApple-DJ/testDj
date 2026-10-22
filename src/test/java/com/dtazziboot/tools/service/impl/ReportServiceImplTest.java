package com.dtazziboot.tools.service.impl;

import com.dtazziboot.tools.common.exception.BizException;
import com.dtazziboot.tools.dao.mapper.InvokeLogMapper;
import com.dtazziboot.tools.model.dto.DistributionItem;
import com.dtazziboot.tools.model.dto.ReportQuery;
import com.dtazziboot.tools.model.dto.TrendPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * {@link ReportServiceImpl} 单元测试。
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private InvokeLogMapper invokeLogMapper;

    private ReportServiceImpl reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportServiceImpl(invokeLogMapper);
    }

    @Test
    void should_returnTrend_when_queryValid() {
        ReportQuery query = new ReportQuery();
        when(invokeLogMapper.listTrend(any(LocalDateTime.class), any(LocalDateTime.class), isNull()))
                .thenReturn(List.of(new TrendPoint("2026-10-22", 12L)));

        List<TrendPoint> result = reportService.trend(query);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCount()).isEqualTo(12L);
    }

    @Test
    void should_returnDistributionByType_when_dimensionType() {
        ReportQuery query = new ReportQuery();
        query.setDimension("TYPE");
        when(invokeLogMapper.listDistributionByType(any(), any(), isNull()))
                .thenReturn(List.of(new DistributionItem("EMPLOYEE", 30L)));

        List<DistributionItem> result = reportService.distribution(query);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getKey()).isEqualTo("EMPLOYEE");
        verify(invokeLogMapper).listDistributionByType(any(), any(), isNull());
    }

    @Test
    void should_returnDistributionByLevel_when_dimensionLevel() {
        ReportQuery query = new ReportQuery();
        query.setDimension("LEVEL");
        when(invokeLogMapper.listDistributionByLevel(any(), any(), isNull()))
                .thenReturn(List.of(new DistributionItem("P6", 5L)));

        List<DistributionItem> result = reportService.distribution(query);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getKey()).isEqualTo("P6");
    }

    @Test
    void should_returnDistributionByDept_when_dimensionDept() {
        ReportQuery query = new ReportQuery();
        query.setDimension("DEPT");
        when(invokeLogMapper.listDistributionByDept(any(), any(), isNull()))
                .thenReturn(List.of(new DistributionItem("数科", 8L)));

        List<DistributionItem> result = reportService.distribution(query);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getKey()).isEqualTo("数科");
    }

    @Test
    void should_throwException_when_dimensionUnsupported() {
        ReportQuery query = new ReportQuery();
        query.setDimension("UNKNOWN");

        assertThatThrownBy(() -> reportService.distribution(query))
                .isInstanceOf(BizException.class);
    }
}