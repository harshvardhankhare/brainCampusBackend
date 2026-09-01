package com.braincampus.exam.dto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ExamResponse {

    private Long id;

    private String name;

    private String academicYear;

    private Long classId;

    private String className;

    private String section;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean active;
}