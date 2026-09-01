package com.braincampus.classSubject.dto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClassSubjectResponse {

    private Long id;

    private Long classId;

    private String className;

    private String section;

    private Long subjectId;

    private String subjectName;

    private String subjectCode;

    private String academicYear;

    private Integer weeklyPeriods;

    private Boolean active;

    private Long teacherId;

    private String teacherName;
}