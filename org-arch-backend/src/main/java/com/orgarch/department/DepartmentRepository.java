package com.orgarch.department;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findByParentIdAndIsDeletedOrderBySortOrderAscIdAsc(Long parentId, Integer isDeleted);

    @Query("select d from Department d where d.isDeleted = 0 order by d.sortOrder asc, d.id asc")
    List<Department> findAllActive();

    @Query("select d from Department d where d.isDeleted = 0 and d.path like :prefix")
    List<Department> findByPathPrefix(@Param("prefix") String prefix);

    long countByParentIdAndIsDeleted(Long parentId, Integer isDeleted);
}
