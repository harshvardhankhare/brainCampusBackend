package com.braincampus.student.repository;
import com.braincampus.student.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Page<Student> findAllByTenantId(Long tenantId, Pageable pageable);
    Optional<Student> findByIdAndTenantId(Long id, Long tenantId);
    Optional<Student> findByRoleNumberAndTenantId(String roleNumber, Long tenantId);
    boolean existsByRoleNumberAndTenantId(String roleNumber, Long tenantId);
    @Query("""
        SELECT MAX(CAST(s.roleNumber AS integer))
        FROM Student s
        WHERE s.tenant.id = :tenantId
        """)
    Integer findMaxRoleNumberByTenantId(@Param("tenantId") Long tenantId);
    @Query(
            value = """
            SELECT s
            FROM Student s
            WHERE s.tenant.id = :tenantId
              AND (:classId IS NULL OR s.schoolClass.id = :classId)
              AND (:academicYear IS NULL OR s.schoolClass.academicYear = :academicYear)
            """,
            countQuery = """
            SELECT count(s)
            FROM Student s
            WHERE s.tenant.id = :tenantId
              AND (:classId IS NULL OR s.schoolClass.id = :classId)
              AND (:academicYear IS NULL OR s.schoolClass.academicYear = :academicYear)
            """
    )
    Page<Student> findStudentsWithFilter(
            @Param("tenantId") Long tenantId,
            @Param("classId") Long classId,
            @Param("academicYear") String academicYear,
            Pageable pageable
    );

    @Query("""
        SELECT s
        FROM Student s
        WHERE s.tenant.id = :tenantId
          AND s.schoolClass.id = :classId
          AND (s.deleted = false OR s.deleted IS NULL)
          AND (s.active = true OR s.active IS NULL)
        """)
    List<Student> findAllActiveByTenantIdAndClassId(
            @Param("tenantId") Long tenantId,
            @Param("classId") Long classId
    );
    List<Student> findAllByTenantId(Long tenantId);
}