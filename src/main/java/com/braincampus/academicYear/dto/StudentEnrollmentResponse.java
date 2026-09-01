package com.braincampus.academicYear.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StudentEnrollmentResponse {

    private Long id;

    private Long studentId;
    private String studentName;
    private String roleNumber;

    private Long academicYearId;
    private String academicYear;

    private Long classId;
    private String className;
    private String section;

    private Boolean active;
}