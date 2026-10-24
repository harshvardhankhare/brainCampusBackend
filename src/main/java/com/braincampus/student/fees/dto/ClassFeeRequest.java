package com.braincampus.student.fees.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
public class ClassFeeRequest {

    @NotNull
    private Long classId;

    private String academicYear;

    @NotNull
    private Long feeTypeId;

    private Integer feeMonth;

    @NotNull
    private BigDecimal amount;

    private LocalDate dueDate;

    private String description;
}