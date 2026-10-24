package com.braincampus.student.fees.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class StudentFeeResponse {

    private Long id;
    private Long studentId;
    private String roleNumber;
    private String studentName;
    private String academicYear;
    private String feeType;

    private Integer feeMonth;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private BigDecimal pendingAmount;
    private String status;
    private LocalDate dueDate;
    private String description;
    private String schoolCode;
}