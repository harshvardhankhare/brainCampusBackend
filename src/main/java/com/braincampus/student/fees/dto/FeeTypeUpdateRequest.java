package com.braincampus.student.fees.dto;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeeTypeUpdateRequest {
    @Size(max = 60)
    private String name;

    @Size(max = 30)
    private String code;

    private Boolean isRecurring;

    private Boolean isActive;

    private Integer sortOrder;
}