package com.braincampus.management.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class SalaryPaymentRequest {

    @NotNull(message = "Staff ID is required")
    private Long staffId;

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @NotNull(message = "Year is required")
    @Min(value = 2000, message = "Invalid year")
    private Integer year;

    @NotNull(message = "Amount is required")
    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "Amount cannot be negative"
    )
    private BigDecimal amount;

    private LocalDate paymentDate;

    @NotNull(message = "Payment status is required")
    private com.braincampus.management.entity.SalaryPaymentStatus status;

    private String remarks;
}