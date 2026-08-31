package com.braincampus.student.fees.dto;

import com.braincampus.student.fees.FeePaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class FeePaymentResponse {

    private Long id;

    private Long feeId;

    private Long studentId;

    private String roleNumber;

    private String studentName;

    private BigDecimal amount;

    private LocalDate paymentDate;

    private FeePaymentMethod paymentMethod;

    private String transactionReference;

    private String remarks;

    private String schoolCode;
}