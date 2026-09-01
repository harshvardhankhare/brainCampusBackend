package com.braincampus.academicYear.dto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class AcademicYearResponse {

    private Long id;

    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean active;
}