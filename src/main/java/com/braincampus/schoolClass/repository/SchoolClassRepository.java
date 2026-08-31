package com.braincampus.schoolClass.repository;

import com.braincampus.schoolClass.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {

    List<SchoolClass> findAllByTenantId(Long tenantId);

    Optional<SchoolClass> findByIdAndTenantId(
            Long id,
            Long tenantId
    );

    Optional<SchoolClass> findByNameAndSectionAndAcademicYearAndTenantId(
            String name,
            String section,
            String academicYear,
            Long tenantId
    );

    boolean existsByNameAndSectionAndAcademicYearAndTenantId(
            String name,
            String section,
            String academicYear,
            Long tenantId
    );
}