package com.braincampus.student.fees.dto;

import com.braincampus.student.fees.FeeType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassFeeRequest {

    @NotNull(message = "Class ID is required")
    private Long classId;

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
    @Min(value = 1, message = "Fee month must be between 1 and 12")
    @Max(value = 12, message = "Fee month must be between 1 and 12")
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
