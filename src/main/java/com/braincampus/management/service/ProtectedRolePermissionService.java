package com.braincampus.management.service;

import com.braincampus.auth.entity.Permission;
import com.braincampus.auth.entity.Role;
import com.braincampus.auth.repository.PermissionRepository;
import com.braincampus.auth.repository.RoleRepository;
import com.braincampus.common.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProtectedRolePermissionService {

    private static final List<RoleType> PROTECTED_ROLES =
            List.of(RoleType.ADMIN, RoleType.SUPER_ADMIN);

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    public void synchronizeAllPermissions() {
        Set<Permission> allPermissions = permissionRepository.findAll().stream()
                .filter(permission -> !Boolean.TRUE.equals(permission.getDeleted()))
                .collect(java.util.stream.Collectors.toCollection(HashSet::new));
        roleRepository.findByNameIn(PROTECTED_ROLES).forEach(role -> {
            Set<Permission> rolePermissions = new HashSet<>(role.getPermissions());
            if (rolePermissions.addAll(allPermissions)) {
                role.setPermissions(rolePermissions);
                roleRepository.save(role);
            }
        });
    }

    @Transactional
    public void grantPermissionToProtectedRoles(Permission permission) {
        roleRepository.findByNameIn(PROTECTED_ROLES).forEach(role -> {
            Set<Permission> rolePermissions = new HashSet<>(role.getPermissions());
            if (rolePermissions.add(permission)) {
                role.setPermissions(rolePermissions);
                roleRepository.save(role);
            }
        });
    }
}
