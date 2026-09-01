package com.braincampus.management.repository;

import com.braincampus.management.entity.SalaryStructure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SalaryStructureRepository
        extends JpaRepository<SalaryStructure, Long> {

    List<SalaryStructure> findAllByTenantId(Long tenantId);

    List<SalaryStructure> findAllByStaffIdAndTenantId(
            Long staffId,
            Long tenantId
    );

    Optional<SalaryStructure> findByIdAndTenantId(
            Long id,
            Long tenantId
    );

    Optional<SalaryStructure> findByStaffIdAndActiveTrueAndTenantId(
            Long staffId,
            Long tenantId
    );

    List<SalaryStructure> findAllByStaffIdAndTenantIdAndEffectiveFromLessThanEqual(
            Long staffId,
            Long tenantId,
            LocalDate date
    );
}