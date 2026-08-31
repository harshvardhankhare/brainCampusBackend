package com.braincampus.student.fees.dto;

import com.braincampus.student.fees.FeePaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FeePaymentRequest {

    @NotNull(message = "Fee ID is required")
    private Long feeId;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(
            value = "0.01",
            message = "Payment amount must be greater than zero"
    )
    private BigDecimal amount;

    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;

    @NotNull(message = "Payment method is required")
    private FeePaymentMethod paymentMethod;

    @Size(
            max = 100,
            message = "Transaction reference cannot exceed 100 characters"
    )
    private String transactionReference;

    @Size(
            max = 500,
            message = "Remarks cannot exceed 500 characters"
    )
    private String remarks;
}