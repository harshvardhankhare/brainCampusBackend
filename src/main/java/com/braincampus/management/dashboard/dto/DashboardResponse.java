package com.braincampus.management.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class DashboardResponse {

    private long totalStudents;

    private long totalStaff;

    private DashboardFeeSummary fees;

    private DashboardSalarySummary salary;

    private DashboardExpenseSummary expenses;

    private BigDecimal totalIncome;

    private BigDecimal totalExpenses;

    private BigDecimal netAmount;
}