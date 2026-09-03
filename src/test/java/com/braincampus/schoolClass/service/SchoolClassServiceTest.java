package com.braincampus.schoolClass.service;

import com.braincampus.auth.entity.Tenant;
import com.braincampus.auth.entity.User;
import com.braincampus.auth.repository.UserRepository;
import com.braincampus.common.enums.RoleType;
import com.braincampus.auth.entity.Role;
import com.braincampus.exception.DuplicateResourceException;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.schoolClass.dto.SchoolClassRequest;
import com.braincampus.schoolClass.dto.SchoolClassResponse;
import com.braincampus.schoolClass.entity.SchoolClass;
import com.braincampus.schoolClass.repository.SchoolClassRepository;
import com.braincampus.security.userDetails.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchoolClassServiceTest {

    @Mock
    private SchoolClassRepository schoolClassRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SchoolClassService schoolClassService;

    private final Long tenantId = 1L;
    private final Long userId = 99L;
    private Tenant tenant;
    private User user;
    private SchoolClass schoolClass;

    @BeforeEach
    void setUp() {
        tenant = Tenant.builder()
                .schoolCode("SCH001")
                .name("Test School")
                .build();
        tenant.setId(tenantId);

        Role role = Role.builder()
                .name(RoleType.ADMIN)
                .permissions(new HashSet<>())
                .tenant(tenant)
                .build();

        user = User.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin@test.com")
                .password("encodedPassword")
                .tenant(tenant)
                .role(role)
                .build();
        user.setId(userId);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        schoolClass = SchoolClass.builder()
                .name("Class 10")
                .section("A")
                .academicYear("2024-2025")
                .active(true)
                .tenant(tenant)
                .build();
        schoolClass.setId(10L);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void create_Success() {
        SchoolClassRequest request = new SchoolClassRequest();
        request.setName("Class 10");
        request.setSection("A");
        request.setAcademicYear("2024-2025");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(schoolClassRepository.existsByNameAndSectionAndAcademicYearAndTenantId("Class 10", "A", "2024-2025", tenantId))
                .thenReturn(false);
        when(schoolClassRepository.save(any(SchoolClass.class))).thenAnswer(inv -> {
            SchoolClass sc = inv.getArgument(0);
            sc.setId(10L);
            return sc;
        });

        SchoolClassResponse response = schoolClassService.create(request);

        assertNotNull(response);
        assertEquals("Class 10", response.getName());
        assertEquals("A", response.getSection());
        assertEquals("2024-2025", response.getAcademicYear());
        assertTrue(response.getActive());
        verify(schoolClassRepository, times(1)).save(any(SchoolClass.class));
    }

    @Test
    void create_Duplicate_ThrowsDuplicateResourceException() {
        SchoolClassRequest request = new SchoolClassRequest();
        request.setName("Class 10");
        request.setSection("A");
        request.setAcademicYear("2024-2025");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(schoolClassRepository.existsByNameAndSectionAndAcademicYearAndTenantId("Class 10", "A", "2024-2025", tenantId))
                .thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> schoolClassService.create(request));
        verify(schoolClassRepository, never()).save(any(SchoolClass.class));
    }

    @Test
    void getAll_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(schoolClassRepository.findAllByTenantId(tenantId)).thenReturn(List.of(schoolClass));

        List<SchoolClassResponse> list = schoolClassService.getAll();

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("Class 10", list.get(0).getName());
    }

    @Test
    void getById_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(schoolClassRepository.findByIdAndTenantId(10L, tenantId)).thenReturn(Optional.of(schoolClass));

        SchoolClassResponse response = schoolClassService.getById(10L);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Class 10", response.getName());
    }

    @Test
    void getById_NotFound_ThrowsResourceNotFoundException() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(schoolClassRepository.findByIdAndTenantId(999L, tenantId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> schoolClassService.getById(999L));
    }

    @Test
    void delete_SoftDelete_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(schoolClassRepository.findByIdAndTenantId(10L, tenantId)).thenReturn(Optional.of(schoolClass));
        when(schoolClassRepository.save(any(SchoolClass.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> schoolClassService.delete(10L));
        assertTrue(schoolClass.getDeleted());
        assertFalse(schoolClass.getActive());
        verify(schoolClassRepository, times(1)).save(schoolClass);
    }
}
