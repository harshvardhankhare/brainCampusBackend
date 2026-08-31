package com.braincampus.schoolClass.dto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SchoolClassResponse {

    private Long id;

    private String name;

    private String section;

    private String academicYear;

    private Boolean active;
}