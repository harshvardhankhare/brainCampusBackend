package com.braincampus.student.fees.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeeTypeRequest {
    @NotBlank
    @Size(max = 60)
    private String name;

    @Size(max = 30)
    private String code;

    private Boolean isRecurring;

    private Integer sortOrder;
}
