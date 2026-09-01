package com.braincampus.exam.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ReportCardSubjectResponse {

    private Long subjectId;
    private String subjectName;
    private String subjectCode;

    private Integer maxMarks;
    private Integer passingMarks;

    private BigDecimal marksObtained;

    private String grade;

    private Boolean passed;
}