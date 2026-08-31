package com.braincampus.student.fees.service;

import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.security.SecurityUtils;
import com.braincampus.student.entity.Student;
import com.braincampus.student.fees.dto.FeePaymentRequest;
import com.braincampus.student.fees.dto.FeePaymentResponse;
import com.braincampus.student.fees.entity.FeePayment;
import com.braincampus.student.fees.entity.StudentFee;
import com.braincampus.student.fees.repository.FeePaymentRepository;
import com.braincampus.student.fees.repository.StudentFeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FeePaymentService {

    private final FeePaymentRepository paymentRepository;
    private final StudentFeeRepository feeRepository;

    public FeePaymentResponse create(FeePaymentRequest request) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        StudentFee fee = feeRepository
                .findByIdAndTenantId(
                        request.getFeeId(),
                        tenantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Fee not found"
                        )
                );

        BigDecimal paidAmount =
                paymentRepository
                        .findTotalPaidByTenantIdAndStudentFeeId(
                                tenantId,
                                fee.getId()
                        );

        if (paidAmount == null) {
            paidAmount = BigDecimal.ZERO;
        }

        BigDecimal remainingAmount =
                fee.getAmount().subtract(paidAmount);

        if (request.getAmount().compareTo(remainingAmount) > 0) {
            throw new IllegalArgumentException(
                    "Payment amount cannot exceed pending fee amount"
            );
        }

        FeePayment payment = FeePayment.builder()
                .tenant(fee.getTenant())
                .studentFee(fee)
                .amount(request.getAmount())
                .paymentDate(request.getPaymentDate())
                .paymentMethod(request.getPaymentMethod())
                .transactionReference(
                        request.getTransactionReference()
                )
                .remarks(request.getRemarks())
                .build();

        payment = paymentRepository.save(payment);

        return mapToResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<FeePaymentResponse> getPaymentsByFee(
            Long feeId
    ) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        StudentFee fee = feeRepository
                .findByIdAndTenantId(
                        feeId,
                        tenantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Fee not found"
                        )
                );

        return paymentRepository
                .findAllByTenantIdAndStudentFeeId(
                        tenantId,
                        fee.getId()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private FeePaymentResponse mapToResponse(
            FeePayment payment
    ) {

        Student student =
                payment.getStudentFee().getStudent();

        return FeePaymentResponse.builder()
                .id(payment.getId())
                .feeId(payment.getStudentFee().getId())
                .studentId(student.getId())
                .roleNumber(student.getRoleNumber())
                .studentName(buildStudentName(student))
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .paymentMethod(payment.getPaymentMethod())
                .transactionReference(
                        payment.getTransactionReference()
                )
                .remarks(payment.getRemarks())
                .schoolCode(
                        payment.getTenant().getSchoolCode()
                )
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