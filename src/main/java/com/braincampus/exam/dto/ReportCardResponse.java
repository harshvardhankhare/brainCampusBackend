package com.braincampus.exam.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class ReportCardResponse {

    private Long studentId;
    private String studentName;
    private String roleNumber;

    private Long classId;
    private String className;
    private String section;

    private Long examId;
    private String examName;
    private String academicYear;

    private List<ReportCardSubjectResponse> subjects;

    private BigDecimal totalMarks;
    private Integer maximumMarks;

    private BigDecimal percentage;

    private String grade;

    private String result;
}