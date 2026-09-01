package com.braincampus.exam.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class StudentResultRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Exam subject ID is required")
    private Long examSubjectId;

    @NotNull(message = "Marks obtained are required")
    @DecimalMin(
            value = "0.0",
            message = "Marks cannot be negative"
    )
    private BigDecimal marksObtained;

    private String remarks;
}