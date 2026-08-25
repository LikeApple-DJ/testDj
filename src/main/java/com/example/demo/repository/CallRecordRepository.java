package com.example.demo.repository;

import com.example.demo.model.CallRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CallRecordRepository extends JpaRepository<CallRecord, Long> {

    // 按人员类型统计
    @Query("SELECT c.callerType AS name, COUNT(c) AS value FROM CallRecord c " +
           "WHERE (:start IS NULL OR c.callTime >= :start) " +
           "AND (:end IS NULL OR c.callTime <= :end) " +
           "GROUP BY c.callerType ORDER BY COUNT(c) DESC")
    List<Object[]> countByCallerType(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);

    // 按人员层级统计
    @Query("SELECT c.callerLevel AS name, COUNT(c) AS value FROM CallRecord c " +
           "WHERE (:start IS NULL OR c.callTime >= :start) " +
           "AND (:end IS NULL OR c.callTime <= :end) " +
           "GROUP BY c.callerLevel ORDER BY COUNT(c) DESC")
    List<Object[]> countByCallerLevel(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

    // 按人员部门统计
    @Query("SELECT c.callerDept AS name, COUNT(c) AS value FROM CallRecord c " +
           "WHERE (:start IS NULL OR c.callTime >= :start) " +
           "AND (:end IS NULL OR c.callTime <= :end) " +
           "GROUP BY c.callerDept ORDER BY COUNT(c) DESC")
    List<Object[]> countByCallerDept(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);

    // 按时间趋势统计（按天聚合）
    @Query("SELECT FUNCTION('FORMATDATETIME', c.callTime, 'yyyy-MM-dd') AS name, COUNT(c) AS value FROM CallRecord c " +
           "WHERE (:start IS NULL OR c.callTime >= :start) " +
           "AND (:end IS NULL OR c.callTime <= :end) " +
           "GROUP BY FUNCTION('FORMATDATETIME', c.callTime, 'yyyy-MM-dd') ORDER BY name ASC")
    List<Object[]> countByTimeTrend(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);

    // 获取所有调用记录用于导出
    @Query("SELECT c FROM CallRecord c WHERE c.apiName LIKE :apiPattern ORDER BY c.callTime DESC")
    List<CallRecord> findByApiNameLike(@Param("apiPattern") String apiPattern);

    // 获取所有不同的调用人列表
    @Query("SELECT DISTINCT c.callerId, c.callerType, c.callerLevel, c.callerDept FROM CallRecord c")
    List<Object[]> findDistinctCallers();
}