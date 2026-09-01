package com.braincampus.academicYear.repository;
import com.braincampus.academicYear.entity.StudentEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentEnrollmentRepository
        extends JpaRepository<StudentEnrollment, Long> {

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