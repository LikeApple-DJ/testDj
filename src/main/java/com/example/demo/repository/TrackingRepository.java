package com.example.demo.repository;

import com.example.demo.entity.TrackingRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrackingRepository extends JpaRepository<TrackingRecord, Long> {

    long countByCallerType(String callerType);

    long countByCallerLevel(String callerLevel);

    long countByCallerDept(String callerDept);

    @Query("SELECT FUNCTION('FORMATDATETIME', t.callTime, 'yyyy-MM-dd') AS label, COUNT(t) AS value " +
           "FROM TrackingRecord t WHERE t.callTime BETWEEN :start AND :end GROUP BY label ORDER BY label")
    List<Object[]> countByTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    Page<TrackingRecord> findByApiName(String apiName, Pageable pageable);

    Page<TrackingRecord> findByCallerName(String callerName, Pageable pageable);

    Page<TrackingRecord> findByApiNameAndCallerName(String apiName, String callerName, Pageable pageable);

    List<TrackingRecord> findByApiName(String apiName);

    List<TrackingRecord> findByApiNameAndCallerName(String apiName, String callerName);

    @Query("SELECT t.callerType, COUNT(t) FROM TrackingRecord t WHERE t.apiName = :apiName GROUP BY t.callerType")
    List<Object[]> countByApiNameGroupByCallerType(@Param("apiName") String apiName);

    @Query("SELECT t.callerLevel, COUNT(t) FROM TrackingRecord t WHERE t.apiName = :apiName GROUP BY t.callerLevel")
    List<Object[]> countByApiNameGroupByCallerLevel(@Param("apiName") String apiName);

    @Query("SELECT t.callerDept, COUNT(t) FROM TrackingRecord t WHERE t.apiName = :apiName GROUP BY t.callerDept")
    List<Object[]> countByApiNameGroupByCallerDept(@Param("apiName") String apiName);

    @Query("SELECT t.callerType, COUNT(t) FROM TrackingRecord t GROUP BY t.callerType")
    List<Object[]> countGroupByCallerType();

    @Query("SELECT t.callerLevel, COUNT(t) FROM TrackingRecord t GROUP BY t.callerLevel")
    List<Object[]> countGroupByCallerLevel();

    @Query("SELECT t.callerDept, COUNT(t) FROM TrackingRecord t GROUP BY t.callerDept")
    List<Object[]> countGroupByCallerDept();
}