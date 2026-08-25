package com.testdj.repository;

import com.testdj.entity.ApiCallLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApiCallLogRepository extends JpaRepository<ApiCallLog, Long> {

    List<ApiCallLog> findByApiName(String apiName);

    @Query("SELECT a.department AS dim, COUNT(a) AS cnt FROM ApiCallLog a GROUP BY a.department")
    List<Object[]> countByDepartment();

    @Query("SELECT a.level AS dim, COUNT(a) AS cnt FROM ApiCallLog a GROUP BY a.level")
    List<Object[]> countByLevel();

    @Query("SELECT a.type AS dim, COUNT(a) AS cnt FROM ApiCallLog a GROUP BY a.type")
    List<Object[]> countByType();

    @Query("SELECT FUNCTION('DATE', a.callTime) AS dt, COUNT(a) AS cnt FROM ApiCallLog a GROUP BY FUNCTION('DATE', a.callTime) ORDER BY dt")
    List<Object[]> countByDate();

    @Query("SELECT a FROM ApiCallLog a WHERE a.callTime BETWEEN :start AND :end")
    List<ApiCallLog> findByCallTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}