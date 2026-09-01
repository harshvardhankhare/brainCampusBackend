package com.braincampus.exam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ExamRequest {

    @NotBlank(message = "Exam name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Academic year is required")
    @Size(max = 20)
    private String academicYear;

    @NotNull(message = "Class ID is required")
    private Long classId;

    private LocalDate startDate;

    private LocalDate endDate;
}