package com.braincampus.academicYear.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PromotionResponse {

    private Long fromAcademicYearId;
    private String fromAcademicYear;

    private Long fromClassId;
    private String fromClassName;
    private String fromSection;

    private Long toAcademicYearId;
    private String toAcademicYear;

    private Long toClassId;
    private String toClassName;
    private String toSection;

    private Integer totalStudents;
    private Integer promotedStudents;
}