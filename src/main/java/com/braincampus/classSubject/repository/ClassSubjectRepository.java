package com.braincampus.classSubject.repository;

import com.braincampus.classSubject.entity.ClassSubject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassSubjectRepository
        extends JpaRepository<ClassSubject, Long> {

    List<ClassSubject> findAllByTenantId(Long tenantId);

    List<ClassSubject> findAllBySchoolClassIdAndTenantId(
            Long classId,
            Long tenantId
    );

    List<ClassSubject> findAllBySubjectIdAndTenantId(
            Long subjectId,
            Long tenantId
    );

    Optional<ClassSubject> findByIdAndTenantId(
            Long id,
            Long tenantId
    );

    boolean existsBySchoolClassIdAndSubjectIdAndAcademicYearAndTenantId(
            Long classId,
            Long subjectId,
            String academicYear,
            Long tenantId
    );
}