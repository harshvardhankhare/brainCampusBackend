package com.braincampus.management.dashboard.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class DashboardSalarySummary {

    private BigDecimal totalDue;

    private BigDecimal totalPaid;

    private BigDecimal totalPending;

    private long staffCount;

    private long staffPaid;

    private long staffPending;
}