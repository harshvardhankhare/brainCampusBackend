package com.braincampus.management.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class SalaryStructureResponse {

    private Long id;

    private Long staffId;
    private String employeeCode;
    private String staffName;

    private BigDecimal basicSalary;
    private BigDecimal allowances;
    private BigDecimal deductions;

    private BigDecimal netSalary;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;

    private Boolean active;
}