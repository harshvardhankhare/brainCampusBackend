package com.braincampus.student.fees.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class StudentFeeRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Academic year is required")
    private String academicYear;

    @NotNull(message = "Fee type is required")
    private Long feeTypeId;

    private Integer feeMonth;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    private LocalDate dueDate;

    private String description;
}