package com.braincampus.student.fees.dto;

import com.braincampus.student.fees.FeeType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class StudentFeeResponse {

    private Long id;

    private Long studentId;

    private String roleNumber;

    private String studentName;

    private String academicYear;

    private FeeType feeType;

    private Integer feeMonth;

    private BigDecimal amount;

    private BigDecimal paidAmount;

    private BigDecimal pendingAmount;

    private String status;

    private LocalDate dueDate;

    private String description;

    private String schoolCode;
}