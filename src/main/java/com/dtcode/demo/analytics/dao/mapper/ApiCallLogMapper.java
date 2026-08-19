package com.dtcode.demo.analytics.dao.mapper;

import com.dtcode.demo.analytics.dao.entity.ApiCallLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 接口调用日志 Mapper
 *
 * @author DTCoder
 */
@Mapper
public interface ApiCallLogMapper {

    /**
     * 插入调用日志
     *
     * @param record 调用日志记录
     */
    void insert(ApiCallLogDO record);

    /**
     * 按维度分组统计调用次数
     *
     * @param dimension 统计维度字段名
     * @param apiName   接口名称（可选）
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 分组统计结果列表
     */
    List<Map<String, Object>> selectSummary(@Param("dimension") String dimension,
                                             @Param("apiName") String apiName,
                                             @Param("startDate") Date startDate,
                                             @Param("endDate") Date endDate);

    /**
     * 按时间粒度统计调用趋势
     *
     * @param dateFormat 日期格式表达式
     * @param apiName    接口名称（可选）
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @return 趋势数据点列表
     */
    List<Map<String, Object>> selectTrend(@Param("dateFormat") String dateFormat,
                                           @Param("apiName") String apiName,
                                           @Param("startDate") Date startDate,
                                           @Param("endDate") Date endDate);

    /**
     * 按维度统计调用分布
     *
     * @param dimension 统计维度字段名
     * @param apiName   接口名称（可选）
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 分布数据列表
     */
    List<Map<String, Object>> selectDistribution(@Param("dimension") String dimension,
                                                  @Param("apiName") String apiName,
                                                  @Param("startDate") Date startDate,
                                                  @Param("endDate") Date endDate);

    /**
     * 查询总调用次数
     *
     * @param apiName   接口名称（可选）
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 总调用次数
     */
    Long selectTotalCount(@Param("apiName") String apiName,
                          @Param("startDate") Date startDate,
                          @Param("endDate") Date endDate);
}
