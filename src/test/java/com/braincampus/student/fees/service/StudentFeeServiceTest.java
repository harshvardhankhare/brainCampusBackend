package com.braincampus.student.fees.service;

import com.braincampus.auth.entity.Tenant;
import com.braincampus.exception.DuplicateResourceException;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.schoolClass.entity.SchoolClass;
import com.braincampus.schoolClass.repository.SchoolClassRepository;
import com.braincampus.security.SecurityUtils;
import com.braincampus.student.entity.Student;
import com.braincampus.student.fees.FeeType;
import com.braincampus.student.fees.dto.ClassFeeRequest;
import com.braincampus.student.fees.dto.ClassFeeResponse;
import com.braincampus.student.fees.dto.StudentFeeResponse;
import com.braincampus.student.fees.entity.StudentFee;
import com.braincampus.student.fees.repository.FeePaymentRepository;
import com.braincampus.student.fees.repository.StudentFeeRepository;
import com.braincampus.student.repository.StudentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentFeeServiceTest {

    @Mock
    private StudentFeeRepository feeRepository;

    @Mock
    private FeePaymentRepository paymentRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SchoolClassRepository schoolClassRepository;

    @InjectMocks
    private StudentFeeService feeService;

    private MockedStatic<SecurityUtils> mockedSecurityUtils;
    private final Long tenantId = 1L;

    private Tenant tenant;
    private SchoolClass schoolClass;
    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        mockedSecurityUtils = mockStatic(SecurityUtils.class);
        mockedSecurityUtils.when(SecurityUtils::getCurrentTenantId).thenReturn(tenantId);

        tenant = Tenant.builder()
                .schoolCode("SCH001")
                .schoolName("Test School")
                .build();
        tenant.setId(tenantId);

        schoolClass = SchoolClass.builder()
                .name("Class 10")
                .section("A")
                .academicYear("2024-2025")
                .tenant(tenant)
                .build();
        schoolClass.setId(10L);

        student1 = Student.builder()
                .roleNumber("101")
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .schoolClass(schoolClass)
                .tenant(tenant)
                .active(true)
                .build();
        student1.setId(101L);

        student2 = Student.builder()
                .roleNumber("102")
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@example.com")
                .schoolClass(schoolClass)
                .tenant(tenant)
                .active(true)
                .build();
        student2.setId(102L);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    void createForClass_Success_AllStudentsAssigned() {
        ClassFeeRequest request = ClassFeeRequest.builder()
                .classId(10L)
                .feeType(FeeType.TUITION)
                .feeMonth(4)
                .amount(new BigDecimal("1500.00"))
                .dueDate(LocalDate.of(2024, 4, 15))
                .description("April Tuition")
                .build();

        when(schoolClassRepository.findByIdAndTenantId(10L, tenantId))
                .thenReturn(Optional.of(schoolClass));
        when(studentRepository.findAllActiveByTenantIdAndClassId(tenantId, 10L))
                .thenReturn(List.of(student1, student2));
        when(feeRepository.findAllByTenantIdAndAcademicYearAndClassId(tenantId, "2024-2025", 10L))
                .thenReturn(Collections.emptyList());
        when(feeRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.findTotalPaidByTenantIdAndStudentFeeId(eq(tenantId), any()))
                .thenReturn(BigDecimal.ZERO);

        ClassFeeResponse response = feeService.createForClass(request);

        assertNotNull(response);
        assertEquals(10L, response.getClassId());
        assertEquals("Class 10", response.getClassName());
        assertEquals("A", response.getSection());
        assertEquals("2024-2025", response.getAcademicYear());
        assertEquals(2, response.getTotalStudents());
        assertEquals(2, response.getFeesCreated());
        assertEquals(0, response.getFeesSkipped());
        assertEquals(2, response.getFees().size());
        verify(feeRepository, times(1)).saveAll(anyList());
    }

    @Test
    void createForClass_PartialDuplicate_SkipsExistingFee() {
        ClassFeeRequest request = ClassFeeRequest.builder()
                .classId(10L)
                .feeType(FeeType.TUITION)
                .feeMonth(4)
                .amount(new BigDecimal("1500.00"))
                .build();

        StudentFee existingFee = StudentFee.builder()
                .tenant(tenant)
                .student(student1)
                .academicYear("2024-2025")
                .feeType(FeeType.TUITION)
                .feeMonth(4)
                .amount(new BigDecimal("1500.00"))
                .build();
        existingFee.setId(501L);
        existingFee.setDeleted(false);

        when(schoolClassRepository.findByIdAndTenantId(10L, tenantId))
                .thenReturn(Optional.of(schoolClass));
        when(studentRepository.findAllActiveByTenantIdAndClassId(tenantId, 10L))
                .thenReturn(List.of(student1, student2));
        when(feeRepository.findAllByTenantIdAndAcademicYearAndClassId(tenantId, "2024-2025", 10L))
                .thenReturn(List.of(existingFee));
        when(feeRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRepository.findTotalPaidByTenantIdAndStudentFeeId(eq(tenantId), any()))
                .thenReturn(BigDecimal.ZERO);

        ClassFeeResponse response = feeService.createForClass(request);

        assertNotNull(response);
        assertEquals(2, response.getTotalStudents());
        assertEquals(1, response.getFeesCreated());
        assertEquals(1, response.getFeesSkipped());
        assertEquals(1, response.getFees().size());
        assertEquals("102", response.getFees().get(0).getRoleNumber());
    }

    @Test
    void createForClass_AllDuplicates_ThrowsDuplicateResourceException() {
        ClassFeeRequest request = ClassFeeRequest.builder()
                .classId(10L)
                .feeType(FeeType.TUITION)
                .feeMonth(4)
                .amount(new BigDecimal("1500.00"))
                .build();

        StudentFee existingFee1 = StudentFee.builder()
                .tenant(tenant)
                .student(student1)
                .academicYear("2024-2025")
                .feeType(FeeType.TUITION)
                .feeMonth(4)
                .amount(new BigDecimal("1500.00"))
                .build();
        existingFee1.setId(501L);
        existingFee1.setDeleted(false);

        when(schoolClassRepository.findByIdAndTenantId(10L, tenantId))
                .thenReturn(Optional.of(schoolClass));
        when(studentRepository.findAllActiveByTenantIdAndClassId(tenantId, 10L))
                .thenReturn(List.of(student1));
        when(feeRepository.findAllByTenantIdAndAcademicYearAndClassId(tenantId, "2024-2025", 10L))
                .thenReturn(List.of(existingFee1));

        assertThrows(DuplicateResourceException.class, () -> feeService.createForClass(request));
        verify(feeRepository, never()).saveAll(anyList());
    }

    @Test
    void createForClass_NoStudents_ThrowsResourceNotFoundException() {
        ClassFeeRequest request = ClassFeeRequest.builder()
                .classId(10L)
                .feeType(FeeType.TUITION)
                .feeMonth(4)
                .amount(new BigDecimal("1500.00"))
                .build();

        when(schoolClassRepository.findByIdAndTenantId(10L, tenantId))
                .thenReturn(Optional.of(schoolClass));
        when(studentRepository.findAllActiveByTenantIdAndClassId(tenantId, 10L))
                .thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> feeService.createForClass(request));
    }

    @Test
    void createForClass_ClassNotFound_ThrowsResourceNotFoundException() {
        ClassFeeRequest request = ClassFeeRequest.builder()
                .classId(99L)
                .feeType(FeeType.TUITION)
                .feeMonth(4)
                .amount(new BigDecimal("1500.00"))
                .build();

        when(schoolClassRepository.findByIdAndTenantId(99L, tenantId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> feeService.createForClass(request));
    }

    @Test
    void getFeesByClass_Success() {
        StudentFee fee = StudentFee.builder()
                .tenant(tenant)
                .student(student1)
                .academicYear("2024-2025")
                .feeType(FeeType.TUITION)
                .feeMonth(4)
                .amount(new BigDecimal("1500.00"))
                .build();
        fee.setId(1001L);
        fee.setDeleted(false);

        when(schoolClassRepository.findByIdAndTenantId(10L, tenantId))
                .thenReturn(Optional.of(schoolClass));
        when(feeRepository.findAllByTenantIdAndAcademicYearAndClassId(tenantId, "2024-2025", 10L))
                .thenReturn(List.of(fee));
        when(paymentRepository.findTotalPaidByTenantIdAndStudentFeeId(tenantId, 1001L))
                .thenReturn(BigDecimal.ZERO);

        List<StudentFeeResponse> results = feeService.getFeesByClass(10L, null);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("101", results.get(0).getRoleNumber());
    }
}
