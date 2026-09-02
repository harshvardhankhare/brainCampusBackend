package com.braincampus.student.repository;
import com.braincampus.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findAllByTenantId(Long tenantId);

    Optional<Student> findByIdAndTenantId(
            Long id,
            Long tenantId
    );

    Optional<Student> findByRoleNumberAndTenantId(
            String roleNumber,
            Long tenantId
    );

    boolean existsByRoleNumberAndTenantId(
            String roleNumber,
            Long tenantId
    );
    @Query("""
        SELECT MAX(CAST(s.roleNumber AS integer))
        FROM Student s
        WHERE s.tenant.id = :tenantId
        """)
    Integer findMaxRoleNumberByTenantId(@Param("tenantId") Long tenantId);

    @Query("""
    SELECT s
    FROM Student s
    WHERE s.tenant.id = :tenantId
      AND s.schoolClass.id = :classId
      AND s.schoolClass.academicYear = :academicYear
    """)
    List<Student> findAllByTenantIdAndClassIdAndAcademicYear(
            @Param("tenantId") Long tenantId,
            @Param("classId") Long classId,
            @Param("academicYear") String academicYear
    );
}