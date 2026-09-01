package com.braincampus.management.dto;

import com.braincampus.management.entity.SalaryPaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class SalaryPaymentResponse {

    private Long id;

    private Long staffId;
    private String employeeCode;
    private String staffName;

    private Integer month;
    private Integer year;

    private BigDecimal amount;

    private LocalDate paymentDate;

    private SalaryPaymentStatus status;

    private String remarks;
}