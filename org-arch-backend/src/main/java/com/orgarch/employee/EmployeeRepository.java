package com.orgarch.employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    long countByDeptIdAndIsDeleted(Long deptId, Integer isDeleted);

    boolean existsByEmployeeNoAndIsDeleted(String employeeNo, Integer isDeleted);

    boolean existsByPhoneAndIsDeleted(String phone, Integer isDeleted);

    Optional<Employee> findByIdAndIsDeleted(Long id, Integer isDeleted);

    @Query("""
        select e from Employee e
        where e.isDeleted = 0
          and (:deptId is null or e.deptId = :deptId)
          and (:status is null or e.status = :status)
        order by e.id desc
        """)
    Page<Employee> queryPage(@Param("deptId") Long deptId,
                             @Param("status") String status,
                             Pageable pageable);
}
