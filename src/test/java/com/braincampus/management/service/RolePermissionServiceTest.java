package com.braincampus.management.service;

import com.braincampus.auth.entity.Permission;
import com.braincampus.auth.entity.Role;
import com.braincampus.auth.entity.Tenant;
import com.braincampus.auth.entity.User;
import com.braincampus.auth.repository.PermissionRepository;
import com.braincampus.auth.repository.RoleRepository;
import com.braincampus.common.enums.PermissionType;
import com.braincampus.common.enums.RoleType;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.management.dto.PermissionResponse;
import com.braincampus.management.dto.RolePermissionRequest;
import com.braincampus.management.dto.RolePermissionResponse;
import com.braincampus.security.SecurityUtils;
import com.braincampus.security.userDetails.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolePermissionServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private RolePermissionService rolePermissionService;

    private MockedStatic<SecurityUtils> mockedSecurityUtils;
    private final Long tenantId = 1L;

    private Tenant tenant;
    private Role teacherRole;
    private Role adminRole;
    private Permission perm1;
    private Permission perm2;

    @BeforeEach
    void setUp() {
        mockedSecurityUtils = mockStatic(SecurityUtils.class);
        mockedSecurityUtils.when(SecurityUtils::getCurrentTenantId).thenReturn(tenantId);

        tenant = Tenant.builder()
                .schoolCode("SCH001")
                .schoolName("Test School")
                .build();
        tenant.setId(tenantId);

        User user = User.builder().tenant(tenant).build();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(userDetails);

        perm1 = Permission.builder()
                .name(PermissionType.VIEW_STUDENT)
                .description("View student details")
                .build();
        perm1.setId(1L);

        perm2 = Permission.builder()
                .name(PermissionType.CREATE_STUDENT)
                .description("Create student")
                .build();
        perm2.setId(2L);

        teacherRole = Role.builder()
                .name(RoleType.TEACHER)
                .description("Teacher Role")
                .tenant(tenant)
                .permissions(new HashSet<>(Set.of(perm1)))
                .build();
        teacherRole.setId(20L);

        adminRole = Role.builder()
                .name(RoleType.ADMIN)
                .description("Admin Role")
                .tenant(tenant)
                .permissions(new HashSet<>(Set.of(perm1, perm2)))
                .build();
        adminRole.setId(10L);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    void getAllPermissions_Success() {
        when(permissionRepository.findAll()).thenReturn(List.of(perm1, perm2));

        List<PermissionResponse> response = rolePermissionService.getAllPermissions();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(PermissionType.VIEW_STUDENT, response.get(0).getName());
    }

    @Test
    void getPermissions_Success() {
        when(roleRepository.findById(20L)).thenReturn(Optional.of(teacherRole));

        RolePermissionResponse response = rolePermissionService.getPermissions(20L);

        assertNotNull(response);
        assertEquals(20L, response.getRoleId());
        assertEquals("TEACHER", response.getRole());
        assertEquals(1, response.getPermissions().size());
        assertTrue(response.getPermissions().contains(PermissionType.VIEW_STUDENT));
    }

    @Test
    void getPermissions_NotFound_ThrowsResourceNotFoundException() {
        when(roleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rolePermissionService.getPermissions(999L));
    }

    @Test
    void updatePermissions_Success() {
        RolePermissionRequest request = new RolePermissionRequest();
        request.setPermissions(Set.of(PermissionType.VIEW_STUDENT, PermissionType.CREATE_STUDENT));

        when(roleRepository.findById(20L)).thenReturn(Optional.of(teacherRole));
        when(permissionRepository.findAllByNameIn(request.getPermissions())).thenReturn(List.of(perm1, perm2));
        when(roleRepository.save(any(Role.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> rolePermissionService.updatePermissions(20L, request));
        assertEquals(2, teacherRole.getPermissions().size());
        verify(roleRepository, times(1)).save(teacherRole);
    }

    @Test
    void updatePermissions_AdminRole_ThrowsIllegalArgumentException() {
        RolePermissionRequest request = new RolePermissionRequest();
        request.setPermissions(Set.of(PermissionType.VIEW_STUDENT));

        when(roleRepository.findById(10L)).thenReturn(Optional.of(adminRole));

        assertThrows(IllegalArgumentException.class, () -> rolePermissionService.updatePermissions(10L, request));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void getAllRoles_Success() {
        when(roleRepository.findByTenant_IdAndName(tenantId, RoleType.TEACHER)).thenReturn(Optional.of(teacherRole));
        when(roleRepository.findByTenant_IdAndName(tenantId, RoleType.ACCOUNTANT)).thenReturn(Optional.empty());
        when(roleRepository.findByTenant_IdAndName(tenantId, RoleType.LIBRARIAN)).thenReturn(Optional.empty());

        Role accountantRole = Role.builder().name(RoleType.ACCOUNTANT).tenant(tenant).build();
        accountantRole.setId(30L);
        Role librarianRole = Role.builder().name(RoleType.LIBRARIAN).tenant(tenant).build();
        librarianRole.setId(40L);

        when(roleRepository.save(any(Role.class))).thenAnswer(inv -> inv.getArgument(0));
        when(roleRepository.findAllByTenantId(tenantId)).thenReturn(List.of(adminRole, teacherRole, accountantRole, librarianRole));

        List<RolePermissionResponse> roles = rolePermissionService.getAllRoles();

        assertNotNull(roles);
        assertEquals(4, roles.size());
    }
}
