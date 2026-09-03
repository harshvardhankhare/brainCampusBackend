package com.braincampus.management.service;

import com.braincampus.auth.entity.Permission;
import com.braincampus.auth.entity.Role;
import com.braincampus.auth.repository.PermissionRepository;
import com.braincampus.auth.repository.RoleRepository;
import com.braincampus.common.enums.PermissionType;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.management.dto.PermissionResponse;
import com.braincampus.management.dto.RolePermissionResponse;
import com.braincampus.management.dto.RolePermissionRequest;
import com.braincampus.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RolePermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public void updatePermissions(Long roleId, RolePermissionRequest request) {

        Long tenantId = SecurityUtils.getCurrentTenantId();
        Role role = roleRepository
                        .findById(roleId)
                        .filter(r ->
                                r.getTenant()
                                        .getId()
                                        .equals(tenantId)
                        )
                        .filter(r -> !r.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Role not found"
                                )
                        );

        // Prevent school ADMIN from modifying ADMIN role
        if (role.getName() == com.braincampus.common.enums.RoleType.ADMIN) {
            throw new IllegalArgumentException(
                    "ADMIN role permissions cannot be modified"
            );
        }

        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllByNameIn(request.getPermissions()));
        if (permissions.size() != request.getPermissions().size()) {
            throw new ResourceNotFoundException(
                    "One or more permissions not found"
            );
        }

        role.setPermissions(permissions);

        roleRepository.save(role);
    }
    @Transactional(readOnly = true)
    public RolePermissionResponse getPermissions(Long roleId) {

        Long tenantId = SecurityUtils.getCurrentTenantId();
        Role role = roleRepository
                        .findById(roleId)
                        .filter(r ->
                                r.getTenant()
                                        .getId()
                                        .equals(tenantId)
                        )
                        .filter(r -> !r.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Role not found"
                                )
                        );

        Set<PermissionType> permissions = role.getPermissions()
                        .stream()
                        .map(Permission::getName)
                        .collect(Collectors.toSet());

        return RolePermissionResponse.builder()
                .roleId(role.getId())
                .role(role.getName().name())
                .permissions(permissions)
                .build();
    }
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions() {

        return permissionRepository.findAll()
                .stream()
                .filter(permission -> !permission.getDeleted())
                .map(permission ->
                        PermissionResponse.builder()
                                .name(permission.getName())
                                .description(
                                        permission.getDescription()
                                )
                                .build()
                )
                .toList();
    }

    public List<RolePermissionResponse> getAllRoles() {

        Long tenantId = SecurityUtils.getCurrentTenantId();
        com.braincampus.auth.entity.Tenant tenant =
                SecurityUtils.getCurrentUser().getUser().getTenant();

        // Ensure standard customizable roles exist for this tenant
        List<com.braincampus.common.enums.RoleType> standardRoles = List.of(
                com.braincampus.common.enums.RoleType.TEACHER,
                com.braincampus.common.enums.RoleType.ACCOUNTANT,
                com.braincampus.common.enums.RoleType.LIBRARIAN
        );

        for (com.braincampus.common.enums.RoleType roleType : standardRoles) {
            if (roleRepository.findByTenant_IdAndName(tenantId, roleType).isEmpty()) {
                Role newRole = Role.builder()
                        .name(roleType)
                        .description(roleType.name() + " Role")
                        .tenant(tenant)
                        .permissions(new HashSet<>())
                        .build();
                roleRepository.save(newRole);
            }
        }

        return roleRepository.findAllByTenantId(tenantId)
                .stream()
                .filter(role -> !Boolean.TRUE.equals(role.getDeleted()))
                .map(role -> RolePermissionResponse.builder()
                        .roleId(role.getId())
                        .role(role.getName().name())
                        .permissions(role.getPermissions()
                                .stream()
                                .map(Permission::getName)
                                .collect(Collectors.toSet()))
                        .build())
                .toList();
    }
}