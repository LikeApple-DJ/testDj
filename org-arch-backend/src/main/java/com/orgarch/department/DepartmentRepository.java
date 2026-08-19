package com.orgarch.department;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    List<Department> findByParentIdAndIsDeletedOrderBySortOrderAscIdAsc(Long parentId, Integer isDeleted);

    @Query("select d from Department d where d.isDeleted = 0 order by d.sortOrder asc, d.id asc")
    List<Department> findAllActive();

    /**
     * 按 path 前缀查询子孙节点（含自身）。
     * 使用 concat(:prefix, '%') 拼接通配符，确保 LIKE 能匹配子孙 path（R04）。
     */
    @Query("select d from Department d where d.isDeleted = 0 and d.path like concat(:prefix, '%')")
    List<Department> findByPathPrefix(@Param("prefix") String prefix);

    /**
     * 行级悲观锁查询（design 5.1.3.2 方案C），串行化对同一节点的拖拽，保障子树 path 重算原子性。
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Department d where d.id = :id")
    Optional<Department> findByIdForUpdate(@Param("id") Long id);

    long countByParentIdAndIsDeleted(Long parentId, Integer isDeleted);
}
