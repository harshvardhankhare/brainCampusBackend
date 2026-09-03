package com.braincampus.management.dashboard.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@Builder
public class DashboardFeeSummary {

    private BigDecimal totalFees;

    private BigDecimal collected;

    private BigDecimal remaining;

    private long studentsPaid;

    private long studentsPending;
}