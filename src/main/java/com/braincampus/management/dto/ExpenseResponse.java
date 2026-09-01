package com.braincampus.management.dto;
import com.braincampus.management.entity.ExpensePaymentMethod;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class ExpenseResponse {

    private Long id;

    private String category;

    private BigDecimal amount;

    private LocalDate expenseDate;

    private String description;

    private ExpensePaymentMethod paymentMethod;

    private String referenceNumber;
}