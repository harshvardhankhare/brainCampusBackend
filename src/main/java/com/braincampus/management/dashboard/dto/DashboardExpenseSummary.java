package com.braincampus.management.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class DashboardExpenseSummary {

    private BigDecimal totalExpenses;
}