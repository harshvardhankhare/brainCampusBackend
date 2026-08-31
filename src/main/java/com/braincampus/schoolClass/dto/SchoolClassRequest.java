package com.braincampus.schoolClass.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchoolClassRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String section;

    @NotBlank
    private String academicYear;
}