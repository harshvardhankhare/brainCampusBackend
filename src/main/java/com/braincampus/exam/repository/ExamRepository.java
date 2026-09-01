package com.braincampus.exam.repository;
import com.braincampus.exam.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findAllByTenantId(Long tenantId);

    List<Exam> findAllBySchoolClassIdAndTenantId(
            Long classId,
            Long tenantId
    );

    Optional<Exam> findByIdAndTenantId(
            Long id,
            Long tenantId
    );

    boolean existsBySchoolClassIdAndNameAndAcademicYearAndTenantId(
            Long classId,
            String name,
            String academicYear,
            Long tenantId
    );
}