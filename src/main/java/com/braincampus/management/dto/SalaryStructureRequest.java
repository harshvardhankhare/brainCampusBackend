package com.braincampus.management.dto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class SalaryStructureRequest {

    @NotNull(message = "Staff ID is required")
    private Long staffId;

    @NotNull(message = "Basic salary is required")
    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Basic salary cannot be negative"
    )
    private BigDecimal basicSalary;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Allowances cannot be negative"
    )
    private BigDecimal allowances = BigDecimal.ZERO;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Deductions cannot be negative"
    )
    private BigDecimal deductions = BigDecimal.ZERO;

    @NotNull(message = "Effective from date is required")
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;
}