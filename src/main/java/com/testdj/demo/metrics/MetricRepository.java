package com.testdj.demo.metrics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface MetricRepository extends JpaRepository<MetricEvent, Long> {

    @Query("SELECT new com.testdj.demo.metrics.ReportItem(e.userType, COUNT(e)) FROM MetricEvent e WHERE e.timestamp BETWEEN :start AND :end GROUP BY e.userType")
    List<ReportItem> reportByUserType(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT new com.testdj.demo.metrics.ReportItem(e.userLevel, COUNT(e)) FROM MetricEvent e WHERE e.timestamp BETWEEN :start AND :end GROUP BY e.userLevel")
    List<ReportItem> reportByUserLevel(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT new com.testdj.demo.metrics.ReportItem(e.userDept, COUNT(e)) FROM MetricEvent e WHERE e.timestamp BETWEEN :start AND :end GROUP BY e.userDept")
    List<ReportItem> reportByUserDept(@Param("start") Instant start, @Param("end") Instant end);
}