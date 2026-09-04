package com.braincampus.student.service;

import com.braincampus.auth.entity.Tenant;
import com.braincampus.auth.entity.User;
import com.braincampus.exception.DuplicateResourceException;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.schoolClass.entity.SchoolClass;
import com.braincampus.schoolClass.repository.SchoolClassRepository;
import com.braincampus.security.SecurityUtils;
import com.braincampus.security.userDetails.CustomUserDetails;
import com.braincampus.student.dto.StudentRequest;
import com.braincampus.student.dto.StudentResponse;
import com.braincampus.student.entity.Student;
import com.braincampus.student.repository.StudentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SchoolClassRepository schoolClassRepository;

    @InjectMocks
    private StudentService studentService;

    private MockedStatic<SecurityUtils> mockedSecurityUtils;
    private final Long tenantId = 1L;

    private Tenant tenant;
    private SchoolClass schoolClass;
    private Student student;

    @BeforeEach
    void setUp() {
        mockedSecurityUtils = mockStatic(SecurityUtils.class);
        mockedSecurityUtils.when(SecurityUtils::getCurrentTenantId).thenReturn(tenantId);

        tenant = Tenant.builder()
                .schoolCode("SCH001")
                .schoolName("Test School")
                .build();
        tenant.setId(tenantId);

        User currentUser = User.builder()
                .tenant(tenant)
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(currentUser);
        mockedSecurityUtils.when(SecurityUtils::getCurrentUser).thenReturn(userDetails);

        schoolClass = SchoolClass.builder()
                .name("Class 10")
                .section("A")
                .academicYear("2024-2025")
                .tenant(tenant)
                .build();
        schoolClass.setId(10L);

        student = Student.builder()
                .roleNumber("101")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("9876543210")
                .dateOfBirth(LocalDate.of(2008, 5, 12))
                .address("123 Street")
                .parentName("Robert Doe")
                .parentPhone("9876543211")
                .active(true)
                .tenant(tenant)
                .schoolClass(schoolClass)
                .build();
        student.setId(1001L);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    void create_Success() {
        StudentRequest request = new StudentRequest();
        request.setRoleNumber("101");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPhone("9876543210");
        request.setClassId(10L);

        when(studentRepository.existsByRoleNumberAndTenantId("101", tenantId)).thenReturn(false);
        when(schoolClassRepository.findByIdAndTenantId(10L, tenantId)).thenReturn(Optional.of(schoolClass));
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> {
            Student s = inv.getArgument(0);
            s.setId(1001L);
            return s;
        });

        StudentResponse response = studentService.create(request);

        assertNotNull(response);
        assertEquals("101", response.getRoleNumber());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("Class 10", response.getClassName());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void create_DuplicateRoleNumber_ThrowsDuplicateResourceException() {
        StudentRequest request = new StudentRequest();
        request.setRoleNumber("101");

        when(studentRepository.existsByRoleNumberAndTenantId("101", tenantId)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> studentService.create(request));
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void create_ClassNotFound_ThrowsResourceNotFoundException() {
        StudentRequest request = new StudentRequest();
        request.setRoleNumber("101");
        request.setClassId(999L);

        when(studentRepository.existsByRoleNumberAndTenantId("101", tenantId)).thenReturn(false);
        when(schoolClassRepository.findByIdAndTenantId(999L, tenantId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentService.create(request));
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void getAll_WithClassAndAcademicYear_Success() {
        when(studentRepository.findAllByTenantIdAndClassIdAndAcademicYear(tenantId, 10L, "2024-2025"))
                .thenReturn(List.of(student));

        List<StudentResponse> list = studentService.getAll(10L, "2024-2025");

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("John", list.get(0).getFirstName());
    }

    @Test
    void getAll_WithoutFilter_ReturnsAllTenantStudents() {
        when(studentRepository.findAllByTenantId(tenantId)).thenReturn(List.of(student));

        List<StudentResponse> list = studentService.getAll(null, null);

        assertNotNull(list);
        assertEquals(1, list.size());
    }

    @Test
    void getById_Success() {
        when(studentRepository.findByIdAndTenantId(1001L, tenantId)).thenReturn(Optional.of(student));

        StudentResponse response = studentService.getById(1001L);

        assertNotNull(response);
        assertEquals("101", response.getRoleNumber());
    }

    @Test
    void getById_NotFound_ThrowsResourceNotFoundException() {
        when(studentRepository.findByIdAndTenantId(9999L, tenantId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> studentService.getById(9999L));
    }

    @Test
    void delete_Success() {
        when(studentRepository.findByIdAndTenantId(1001L, tenantId)).thenReturn(Optional.of(student));
        doNothing().when(studentRepository).delete(student);

        assertDoesNotThrow(() -> studentService.delete(1001L));
        verify(studentRepository, times(1)).delete(student);
    }
}
