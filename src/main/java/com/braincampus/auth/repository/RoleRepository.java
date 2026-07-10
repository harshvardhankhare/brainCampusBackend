package com.braincampus.auth.repository;
import com.braincampus.auth.entity.Role;
import com.braincampus.common.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByTenant_IdAndName(Long tenantId, RoleType name);
}
