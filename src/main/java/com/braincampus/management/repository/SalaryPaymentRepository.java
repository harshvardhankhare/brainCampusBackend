package com.braincampus.management.repository;

import com.braincampus.management.entity.SalaryPayment;
import com.braincampus.management.entity.SalaryPaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalaryPaymentRepository
        extends JpaRepository<SalaryPayment, Long> {

    List<SalaryPayment> findAllByTenantId(Long tenantId);

    List<SalaryPayment> findAllByStaffIdAndTenantId(
            Long staffId,
            Long tenantId
    );

    Optional<SalaryPayment> findByIdAndTenantId(
            Long id,
            Long tenantId
    );

    Optional<SalaryPayment> findByStaffIdAndMonthAndYearAndTenantId(
            Long staffId,
            Integer month,
            Integer year,
            Long tenantId
    );

    boolean existsByStaffIdAndMonthAndYearAndTenantId(
            Long staffId,
            Integer month,
            Integer year,
            Long tenantId
    );

    List<SalaryPayment> findAllByMonthAndYearAndTenantId(
            Integer month,
            Integer year,
            Long tenantId
    );

    List<SalaryPayment> findAllByStatusAndTenantId(
            SalaryPaymentStatus status,
            Long tenantId
    );
}