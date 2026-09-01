package com.braincampus.management.dto;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class FeeStudentReportResponse {

    private Long studentId;

    private String roleNumber;

    private String studentName;

    private Long classId;

    private String className;

    private String section;

    private BigDecimal totalFees;

    private BigDecimal totalPaid;

    private BigDecimal remaining;

    private boolean paid;
}