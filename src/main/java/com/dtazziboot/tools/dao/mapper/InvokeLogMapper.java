package com.dtazziboot.tools.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtazziboot.tools.dao.entity.InvokeLogDO;
import com.dtazziboot.tools.model.dto.DistributionItem;
import com.dtazziboot.tools.model.dto.TrendPoint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 埋点流水表数据访问接口。
 *
 * <p>动态维度列无法通过 XML 做参数化列名，因此 distribution 使用可配置静态列映射，
 * 避免 SQL 注入。</p>
 */
@Mapper
public interface InvokeLogMapper extends BaseMapper<InvokeLogDO> {

    /**
     * 按时间聚合调用趋势。
     */
    List<TrendPoint> listTrend(@Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end,
                               @Param("apiCode") String apiCode);

    /**
     * 按人员类型维度聚合分布。
     */
    List<DistributionItem> listDistributionByType(@Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end,
                                                  @Param("apiCode") String apiCode);

    /**
     * 按人员层级维度聚合分布。
     */
    List<DistributionItem> listDistributionByLevel(@Param("start") LocalDateTime start,
                                                   @Param("end") LocalDateTime end,
                                                   @Param("apiCode") String apiCode);

    /**
     * 按人员部门维度聚合分布。
     */
    List<DistributionItem> listDistributionByDept(@Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end,
                                                  @Param("apiCode") String apiCode);
}