package com.braincampus.management.staff.repository;
import com.braincampus.management.staff.entity.Staff;
import com.braincampus.management.staff.entity.StaffType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    List<Staff> findAllByTenantId(Long tenantId);

    List<Staff> findAllByTenantIdAndType(
            Long tenantId,
            StaffType type
    );

    Optional<Staff> findByIdAndTenantId(
            Long id,
            Long tenantId
    );

    Optional<Staff> findByEmployeeCodeAndTenantId(
            String employeeCode,
            Long tenantId
    );

    boolean existsByEmployeeCodeAndTenantId(
            String employeeCode,
            Long tenantId
    );
}