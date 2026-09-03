package com.braincampus.student.fees.service;
import com.braincampus.exception.DuplicateResourceException;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.schoolClass.entity.SchoolClass;
import com.braincampus.schoolClass.repository.SchoolClassRepository;
import com.braincampus.security.SecurityUtils;
import com.braincampus.student.entity.Student;
import com.braincampus.student.fees.dto.ClassFeeRequest;
import com.braincampus.student.fees.dto.ClassFeeResponse;
import com.braincampus.student.fees.dto.StudentFeeRequest;
import com.braincampus.student.fees.dto.StudentFeeResponse;
import com.braincampus.student.fees.entity.StudentFee;
import com.braincampus.student.fees.repository.StudentFeeRepository;
import com.braincampus.student.fees.repository.FeePaymentRepository;
import com.braincampus.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentFeeService {

    private final StudentFeeRepository feeRepository;
    private final FeePaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;

    public StudentFeeResponse create(StudentFeeRequest request) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        // Make sure student belongs to the logged-in school
        Student student = studentRepository
                .findByIdAndTenantId(
                        request.getStudentId(),
                        tenantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found"
                        )
                );

        // Prevent duplicate fee for same student/period
        if (feeRepository
                .existsByTenantIdAndStudentIdAndAcademicYearAndFeeTypeAndFeeMonth(
                        tenantId,
                        student.getId(),
                        request.getAcademicYear(),
                        request.getFeeType(),
                        request.getFeeMonth()
                )) {

            throw new DuplicateResourceException(
                    "Fee already exists for this student and period"
            );
        }

        StudentFee fee = StudentFee.builder()
                .tenant(student.getTenant())
                .student(student)
                .academicYear(request.getAcademicYear())
                .feeType(request.getFeeType())
                .feeMonth(request.getFeeMonth())
                .amount(request.getAmount())
                .dueDate(request.getDueDate())
                .description(request.getDescription())
                .build();

        fee = feeRepository.save(fee);

        return mapToResponse(fee);
    }

    public ClassFeeResponse createForClass(ClassFeeRequest request) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        SchoolClass schoolClass = schoolClassRepository
                .findByIdAndTenantId(request.getClassId(), tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Class not found")
                );

        String academicYear = (request.getAcademicYear() != null && !request.getAcademicYear().isBlank())
                ? request.getAcademicYear().trim()
                : schoolClass.getAcademicYear();

        List<Student> students = studentRepository
                .findAllActiveByTenantIdAndClassId(tenantId, schoolClass.getId());

        if (students.isEmpty()) {
            throw new ResourceNotFoundException("No active students found in this class");
        }

        // Find existing fees for this class and academic year
        List<StudentFee> existingFees = feeRepository
                .findAllByTenantIdAndAcademicYearAndClassId(
                        tenantId,
                        academicYear,
                        schoolClass.getId()
                );

        Set<Long> studentIdsWithFee = existingFees.stream()
                .filter(fee -> !Boolean.TRUE.equals(fee.getDeleted())
                        && fee.getFeeType() == request.getFeeType()
                        && Objects.equals(fee.getFeeMonth(), request.getFeeMonth()))
                .map(fee -> fee.getStudent().getId())
                .collect(Collectors.toSet());

        List<Student> eligibleStudents = students.stream()
                .filter(student -> !studentIdsWithFee.contains(student.getId()))
                .toList();

        if (eligibleStudents.isEmpty()) {
            throw new DuplicateResourceException(
                    "Fee already exists for all students in this class for the specified period"
            );
        }

        List<StudentFee> newFees = eligibleStudents.stream()
                .map(student -> StudentFee.builder()
                        .tenant(student.getTenant())
                        .student(student)
                        .academicYear(academicYear)
                        .feeType(request.getFeeType())
                        .feeMonth(request.getFeeMonth())
                        .amount(request.getAmount())
                        .dueDate(request.getDueDate())
                        .description(request.getDescription())
                        .build())
                .toList();

        List<StudentFee> savedFees = feeRepository.saveAll(newFees);

        List<StudentFeeResponse> feeResponses = savedFees.stream()
                .map(this::mapToResponse)
                .toList();

        int skippedCount = students.size() - eligibleStudents.size();

        return ClassFeeResponse.builder()
                .classId(schoolClass.getId())
                .className(schoolClass.getName())
                .section(schoolClass.getSection())
                .academicYear(academicYear)
                .feeType(request.getFeeType())
                .feeMonth(request.getFeeMonth())
                .amount(request.getAmount())
                .totalStudents(students.size())
                .feesCreated(feeResponses.size())
                .feesSkipped(skippedCount)
                .fees(feeResponses)
                .build();
    }

    @Transactional(readOnly = true)
    public List<StudentFeeResponse> getFeesByClass(
            Long classId,
            String academicYear
    ) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        SchoolClass schoolClass = schoolClassRepository
                .findByIdAndTenantId(classId, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Class not found")
                );

        String year = (academicYear != null && !academicYear.isBlank())
                ? academicYear.trim()
                : schoolClass.getAcademicYear();

        return feeRepository
                .findAllByTenantIdAndAcademicYearAndClassId(
                        tenantId,
                        year,
                        schoolClass.getId()
                )
                .stream()
                .filter(fee -> !Boolean.TRUE.equals(fee.getDeleted()))
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentFeeResponse> getStudentFees(
            Long studentId
    ) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        Student student = studentRepository
                .findByIdAndTenantId(
                        studentId,
                        tenantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found"
                        )
                );

        return feeRepository
                .findAllByTenantIdAndStudentId(
                        tenantId,
                        student.getId()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentFeeResponse> getStudentFeesByAcademicYear(
            Long studentId,
            String academicYear
    ) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        Student student = studentRepository
                .findByIdAndTenantId(
                        studentId,
                        tenantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found"
                        )
                );

        return feeRepository
                .findAllByTenantIdAndStudentIdAndAcademicYear(
                        tenantId,
                        student.getId(),
                        academicYear
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentFeeResponse getById(Long id) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        StudentFee fee = feeRepository
                .findByIdAndTenantId(id, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Fee not found"
                        )
                );

        return mapToResponse(fee);
    }

    private StudentFeeResponse mapToResponse(
            StudentFee fee
    ) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        BigDecimal paidAmount =
                paymentRepository
                        .findTotalPaidByTenantIdAndStudentFeeId(
                                tenantId,
                                fee.getId()
                        );

        if (paidAmount == null) {
            paidAmount = BigDecimal.ZERO;
        }

        BigDecimal pendingAmount =
                fee.getAmount().subtract(paidAmount);

        String status;

        if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
            status = "PENDING";
        } else if (paidAmount.compareTo(fee.getAmount()) >= 0) {
            status = "PAID";
        } else {
            status = "PARTIALLY_PAID";
        }

        return StudentFeeResponse.builder()
                .id(fee.getId())
                .studentId(fee.getStudent().getId())
                .roleNumber(fee.getStudent().getRoleNumber())
                .studentName(buildStudentName(fee.getStudent()))
                .academicYear(fee.getAcademicYear())
                .feeType(fee.getFeeType())
                .feeMonth(fee.getFeeMonth())
                .amount(fee.getAmount())
                .paidAmount(paidAmount)
                .pendingAmount(
                        pendingAmount.max(BigDecimal.ZERO)
                )
                .status(status)
                .dueDate(fee.getDueDate())
                .description(fee.getDescription())
                .schoolCode(fee.getTenant().getSchoolCode())
                .build();
    }

    private String buildStudentName(Student student) {

        if (student.getLastName() == null ||
                student.getLastName().isBlank()) {

            return student.getFirstName();
        }

        return student.getFirstName()
                + " "
                + student.getLastName();
    }
}