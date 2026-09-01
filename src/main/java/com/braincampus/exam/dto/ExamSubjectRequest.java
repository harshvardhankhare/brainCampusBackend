package com.braincampus.exam.dto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ExamSubjectRequest {

    @NotNull(message = "Exam ID is required")
    private Long examId;

    @NotNull(message = "Class subject ID is required")
    private Long classSubjectId;

    private LocalDate examDate;

    @NotNull(message = "Maximum marks are required")
    @Min(value = 1, message = "Maximum marks must be greater than 0")
    private Integer maxMarks;

    @NotNull(message = "Passing marks are required")
    @Min(value = 0, message = "Passing marks cannot be negative")
    private Integer passingMarks;

    private String remarks;
}