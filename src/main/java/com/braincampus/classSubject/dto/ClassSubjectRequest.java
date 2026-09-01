package com.braincampus.classSubject.dto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClassSubjectRequest {

    @NotNull(message = "Class ID is required")
    private Long classId;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotBlank(message = "Academic year is required")
    private String academicYear;

    @NotNull(message = "Weekly periods are required")
    @Min(value = 1, message = "Weekly periods must be at least 1")
    @Max(value = 20, message = "Weekly periods cannot exceed 20")
    private Integer weeklyPeriods;

    private Long teacherId;
}