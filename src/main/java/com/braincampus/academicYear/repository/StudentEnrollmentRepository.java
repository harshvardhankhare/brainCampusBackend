package com.braincampus.academicYear.repository;
import com.braincampus.academicYear.entity.StudentEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentEnrollmentRepository
        extends JpaRepository<StudentEnrollment, Long> {

    @Query(value = """
        SELECT e FROM StudentEnrollment e
        WHERE e.tenant.id = :tenantId
          AND e.deleted = false
          AND (:classId IS NULL OR e.schoolClass.id = :classId)
          AND (:academicYear IS NULL OR e.academicYear.name = :academicYear
               OR e.academicYear.name LIKE concat('%-', :academicYear))
        """,
        countQuery = """
        SELECT count(e) FROM StudentEnrollment e
        WHERE e.tenant.id = :tenantId
          AND e.deleted = false
          AND (:classId IS NULL OR e.schoolClass.id = :classId)
          AND (:academicYear IS NULL OR e.academicYear.name = :academicYear
               OR e.academicYear.name LIKE concat('%-', :academicYear))
        """)
    Page<StudentEnrollment> findAllByFilters(
            @Param("tenantId") Long tenantId,
            @Param("classId") Long classId,
            @Param("academicYear") String academicYear,
            Pageable pageable
    );

    List<StudentEnrollment> findAllByTenantId(Long tenantId);

    List<StudentEnrollment> findAllByStudentIdAndTenantId(
            Long studentId,
            Long tenantId
    );

    List<StudentEnrollment> findAllByAcademicYearIdAndTenantId(
            Long academicYearId,
            Long tenantId
    );

    List<StudentEnrollment> findAllBySchoolClassIdAndAcademicYearIdAndTenantId(
            Long classId,
            Long academicYearId,
            Long tenantId
    );

    Optional<StudentEnrollment> findByIdAndTenantId(
            Long id,
            Long tenantId
    );

    Optional<StudentEnrollment> findByStudentIdAndAcademicYearIdAndTenantId(
            Long studentId,
            Long academicYearId,
            Long tenantId
    );

    boolean existsByStudentIdAndAcademicYearIdAndTenantId(
            Long studentId,
            Long academicYearId,
            Long tenantId
    );
}
