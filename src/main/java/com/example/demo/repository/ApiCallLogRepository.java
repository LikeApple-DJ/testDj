package com.example.demo.repository;

import com.example.demo.entity.ApiCallLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApiCallLogRepository extends JpaRepository<ApiCallLog, Long> {

    List<ApiCallLog> findByApiNameOrderByCallTimeDesc(String apiName);

    @Query("SELECT a.userType, COUNT(a) FROM ApiCallLog a WHERE a.callTime >= :since GROUP BY a.userType")
    List<Object[]> countByUserTypeSince(@Param("since") LocalDateTime since);

    @Query("SELECT a.userLevel, COUNT(a) FROM ApiCallLog a WHERE a.callTime >= :since GROUP BY a.userLevel")
    List<Object[]> countByUserLevelSince(@Param("since") LocalDateTime since);

    @Query("SELECT a.userDept, COUNT(a) FROM ApiCallLog a WHERE a.callTime >= :since GROUP BY a.userDept")
    List<Object[]> countByUserDeptSince(@Param("since") LocalDateTime since);

    @Query("SELECT a.userType, COUNT(a) FROM ApiCallLog a GROUP BY a.userType")
    List<Object[]> countByUserType();

    @Query("SELECT a.userLevel, COUNT(a) FROM ApiCallLog a GROUP BY a.userLevel")
    List<Object[]> countByUserLevel();

    @Query("SELECT a.userDept, COUNT(a) FROM ApiCallLog a GROUP BY a.userDept")
    List<Object[]> countByUserDept();
}
