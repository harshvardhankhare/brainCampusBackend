package com.braincampus.auth.repository;
import com.braincampus.auth.entity.Permission;
import com.braincampus.common.enums.PermissionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByName(PermissionType name);
    boolean existsByName(PermissionType name);
    List<Permission> findAllByNameIn(Set<PermissionType> names);
}
