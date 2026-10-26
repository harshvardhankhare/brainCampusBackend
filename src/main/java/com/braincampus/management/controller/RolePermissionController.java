package com.braincampus.management.controller;
import com.braincampus.auth.entity.Permission;
import com.braincampus.auth.repository.PermissionRepository;
import com.braincampus.common.dto.ApiResponse;
import com.braincampus.management.dto.PermissionResponse;
import com.braincampus.management.dto.RolePermissionRequest;
import com.braincampus.management.dto.RolePermissionResponse;
import com.braincampus.management.service.RolePermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/management/roles")
@RequiredArgsConstructor
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;
    private final PermissionRepository permissionRepository;
    @PutMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('MANAGE_ROLE_PERMISSIONS')")
    public ResponseEntity<ApiResponse<Void>> updatePermissions(@PathVariable Long roleId, @Valid @RequestBody RolePermissionRequest request) {

        rolePermissionService.updatePermissions(roleId, request);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Role permissions updated successfully")
                        .data(null)
                        .build()
        );
    }
    @GetMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('MANAGE_ROLE_PERMISSIONS')")
    public ResponseEntity<ApiResponse<RolePermissionResponse>> getPermissions(@PathVariable Long roleId) {

        RolePermissionResponse response = rolePermissionService.getPermissions(roleId);
        return ResponseEntity.ok(
                ApiResponse.<RolePermissionResponse>builder()
                        .success(true)
                        .message("Role permissions fetched successfully")
                        .data(response)
                        .build()
        );
    }
    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('MANAGE_ROLE_PERMISSIONS')")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getAllPermissions() {

        List<PermissionResponse> response = rolePermissionService.getAllPermissions();
        return ResponseEntity.ok(
                ApiResponse.<List<PermissionResponse>>builder()
                        .success(true)
                        .message("Permissions fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MANAGE_ROLE_PERMISSIONS')")
    public ResponseEntity<ApiResponse<List<RolePermissionResponse>>> getAllRoles() {

        List<RolePermissionResponse> response = rolePermissionService.getAllRoles();
        return ResponseEntity.ok(
                ApiResponse.<List<RolePermissionResponse>>builder()
                        .success(true)
                        .message("Roles fetched successfully")
                        .data(response)
                        .build()
        );
    }
    @PostMapping("/permissions")
    @PreAuthorize("hasAuthority('MANAGE_ROLE_PERMISSIONS')")
    public ResponseEntity<ApiResponse<PermissionResponse>> createPermission(
            @RequestBody PermissionResponse request) {
        if (permissionRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Permission already exists");
        }
        Permission newPerm = Permission.builder()
                .name(request.getName().toUpperCase().trim())
                .description(request.getDescription())
                .build();
        permissionRepository.save(newPerm);

        return ResponseEntity.ok(
                ApiResponse.<PermissionResponse>builder()
                        .success(true)
                        .message("Permission created successfully")
                        .data(PermissionResponse.builder()
                                .name(newPerm.getName())
                                .description(newPerm.getDescription())
                                .build())
                        .build()
        );
    }
}