package com.braincampus.management.dto;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class FeeReportSummaryResponse {

    private BigDecimal totalFees;

    private BigDecimal totalCollected;

    private BigDecimal totalRemaining;

    private long totalStudents;

    private long studentsPaid;

    private long studentsPending;
}