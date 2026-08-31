package com.braincampus.student.fees.service;
import com.braincampus.exception.DuplicateResourceException;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.security.SecurityUtils;
import com.braincampus.student.entity.Student;
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

@Service
@RequiredArgsConstructor
@Transactional
public class StudentFeeService {

    private final StudentFeeRepository feeRepository;
    private final FeePaymentRepository paymentRepository;
    private final StudentRepository studentRepository;

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