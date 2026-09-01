package com.braincampus.academicYear.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromotionRequest {

    @NotNull(message = "From academic year ID is required")
    private Long fromAcademicYearId;

    @NotNull(message = "From class ID is required")
    private Long fromClassId;

    @NotNull(message = "To academic year ID is required")
    private Long toAcademicYearId;

    @NotNull(message = "To class ID is required")
    private Long toClassId;
}