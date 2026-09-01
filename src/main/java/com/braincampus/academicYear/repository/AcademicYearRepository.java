package com.braincampus.academicYear.repository;

import com.braincampus.academicYear.entity.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AcademicYearRepository
        extends JpaRepository<AcademicYear, Long> {

    List<AcademicYear> findAllByTenantId(Long tenantId);

    Optional<AcademicYear> findByIdAndTenantId(
            Long id,
            Long tenantId
    );

    Optional<AcademicYear> findByNameAndTenantId(
            String name,
            Long tenantId
    );

    boolean existsByNameAndTenantId(
            String name,
            Long tenantId
    );

    Optional<AcademicYear> findByActiveTrueAndTenantId(
            Long tenantId
    );
}