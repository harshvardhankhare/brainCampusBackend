package com.braincampus.management.service;
import com.braincampus.auth.entity.Permission;
import com.braincampus.auth.entity.Role;
import com.braincampus.auth.repository.PermissionRepository;
import com.braincampus.auth.repository.RoleRepository;
import com.braincampus.common.enums.RoleType;
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
                .filter(r -> r.getTenant().getId().equals(tenantId))
                .filter(r -> !r.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        if (role.getName() == RoleType.ADMIN) {
            throw new IllegalArgumentException("ADMIN role permissions cannot be modified");
        }

        // Fetch permissions from DB by their string names
        Set<Permission> permissions = new HashSet<>(
                permissionRepository.findAllByNameIn(request.getPermissions())
        );

        if (permissions.size() != request.getPermissions().size()) {
            throw new ResourceNotFoundException("One or more permissions not found");
        }

        role.setPermissions(permissions);
        roleRepository.save(role);
    }

    @Transactional(readOnly = true)
    public RolePermissionResponse getPermissions(Long roleId) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        Role role = roleRepository
                .findById(roleId)
                .filter(r -> r.getTenant().getId().equals(tenantId))
                .filter(r -> !r.getDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        // FIX 1: Explicitly map to String
        Set<String> permissions = role.getPermissions()
                .stream()
                .map(permission -> permission.getName())
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
                .map(permission -> PermissionResponse.builder()
                        .name(permission.getName())
                        .description(permission.getDescription())
                        .build()
                )
                .toList();
    }

    public List<RolePermissionResponse> getAllRoles() {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        var tenant = SecurityUtils.getCurrentUser().getUser().getTenant();

        List<RoleType> standardRoles = List.of(
                RoleType.TEACHER,
                RoleType.ACCOUNTANT,
                RoleType.LIBRARIAN
        );

        for (RoleType roleType : standardRoles) {
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

                        // FIX 2: Explicitly map to String here as well
                        .permissions(role.getPermissions()
                                .stream()
                                .map(permission -> permission.getName())
                                .collect(Collectors.toSet()))

                        .build())
                .toList();
    }
}