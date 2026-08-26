package com.example.demo.repository;

import com.example.demo.model.InvocationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvocationLogRepository extends JpaRepository<InvocationLog, Long> {

    long countByApi(String api);

    long countByUsername(String username);

    @Query("SELECT i.api, COUNT(i) FROM InvocationLog i GROUP BY i.api")
    List<Object[]> countGroupByApi();

    @Query("SELECT i.username, COUNT(i) FROM InvocationLog i GROUP BY i.username")
    List<Object[]> countGroupByUsername();

    @Query("SELECT u.type, COUNT(i) FROM InvocationLog i JOIN UserProfile u ON i.username = u.username GROUP BY u.type")
    List<Object[]> countGroupByUserType();

    @Query("SELECT u.level, COUNT(i) FROM InvocationLog i JOIN UserProfile u ON i.username = u.username GROUP BY u.level")
    List<Object[]> countGroupByUserLevel();

    @Query("SELECT u.department, COUNT(i) FROM InvocationLog i JOIN UserProfile u ON i.username = u.username GROUP BY u.department")
    List<Object[]> countGroupByUserDepartment();

    @Query("SELECT u.type, i.api, COUNT(i) FROM InvocationLog i JOIN UserProfile u ON i.username = u.username GROUP BY u.type, i.api")
    List<Object[]> countGroupByTypeAndApi();
}