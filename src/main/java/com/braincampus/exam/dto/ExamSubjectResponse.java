package com.braincampus.exam.dto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ExamSubjectResponse {

    private Long id;

    private Long examId;
    private String examName;

    private Long classSubjectId;

    private Long classId;
    private String className;
    private String section;

    private Long subjectId;
    private String subjectName;
    private String subjectCode;

    private Long teacherId;
    private String teacherName;

    private String academicYear;

    private LocalDate examDate;

    private Integer maxMarks;
    private Integer passingMarks;

    private String remarks;

    private Boolean active;
}