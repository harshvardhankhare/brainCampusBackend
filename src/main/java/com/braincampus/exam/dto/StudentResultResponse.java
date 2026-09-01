package com.braincampus.exam.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class StudentResultResponse {

    private Long id;

    private Long studentId;
    private String studentName;
    private String roleNumber;

    private Long examSubjectId;

    private Long examId;
    private String examName;

    private Long classId;
    private String className;
    private String section;

    private Long subjectId;
    private String subjectName;
    private String subjectCode;

    private BigDecimal marksObtained;
    private Integer maxMarks;
    private Integer passingMarks;

    private Boolean passed;

    private String remarks;

    private Boolean active;
}