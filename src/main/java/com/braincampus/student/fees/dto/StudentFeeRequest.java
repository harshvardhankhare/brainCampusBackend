package com.braincampus.student.fees.dto;
import com.braincampus.student.fees.FeeType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StudentFeeRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotBlank(message = "Academic year is required")
    @Size(max = 20, message = "Academic year cannot exceed 20 characters")
    private String academicYear;

    @NotNull(message = "Fee type is required")
    private FeeType feeType;

    /*
     * 1 = January
     * 2 = February
     * ...
     * 12 = December
     *
     * Null for non-monthly fees such as EXAM or ADMISSION.
     */
    private Integer feeMonth;

    @NotNull(message = "Fee amount is required")
    @DecimalMin(
            value = "0.01",
            message = "Fee amount must be greater than zero"
    )
    private BigDecimal amount;

    private LocalDate dueDate;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}