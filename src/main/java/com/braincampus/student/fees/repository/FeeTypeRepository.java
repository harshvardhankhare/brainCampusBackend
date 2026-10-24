package com.braincampus.student.fees.repository;
import com.braincampus.student.fees.entity.FeeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeeTypeRepository extends JpaRepository<FeeType, Long> {

    List<FeeType> findAllByTenantIdAndIsActiveTrueOrderBySortOrderAscNameAsc(Long tenantId);

    List<FeeType> findAllByTenantIdOrderBySortOrderAscNameAsc(Long tenantId);

    Optional<FeeType> findByIdAndTenantId(Long id, Long tenantId);

    boolean existsByTenantIdAndNameIgnoreCase(Long tenantId, String name);
}