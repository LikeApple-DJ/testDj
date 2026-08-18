package com.example.demo.repository;

import com.example.demo.entity.MetricsRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MetricsRecordRepository extends JpaRepository<MetricsRecord, Long> {

    @Query("SELECT m.callerType, m.apiPath, COUNT(m) FROM MetricsRecord m " +
           "WHERE (:start IS NULL OR m.callTime >= :start) " +
           "AND (:end IS NULL OR m.callTime <= :end) " +
           "GROUP BY m.callerType, m.apiPath ORDER BY m.callerType, COUNT(m) DESC")
    List<Object[]> countByCallerType(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT m.callerLevel, m.apiPath, COUNT(m) FROM MetricsRecord m " +
           "WHERE (:start IS NULL OR m.callTime >= :start) " +
           "AND (:end IS NULL OR m.callTime <= :end) " +
           "GROUP BY m.callerLevel, m.apiPath ORDER BY m.callerLevel, COUNT(m) DESC")
    List<Object[]> countByCallerLevel(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT m.callerDept, m.apiPath, COUNT(m) FROM MetricsRecord m " +
           "WHERE (:start IS NULL OR m.callTime >= :start) " +
           "AND (:end IS NULL OR m.callTime <= :end) " +
           "GROUP BY m.callerDept, m.apiPath ORDER BY m.callerDept, COUNT(m) DESC")
    List<Object[]> countByCallerDept(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}