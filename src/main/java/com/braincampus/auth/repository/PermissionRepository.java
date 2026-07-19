package com.braincampus.auth.repository;
import com.braincampus.auth.entity.Permission;
import com.braincampus.common.enums.PermissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByName(PermissionType name);
}
