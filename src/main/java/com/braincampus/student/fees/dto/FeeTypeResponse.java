package com.braincampus.student.fees.dto;
import lombok.Builder;
import lombok.Getter;
import java.time.Instant;

@Getter
@Builder
public class FeeTypeResponse {
    private Long id;
    private String name;
    private String code;
    private Boolean isRecurring;
    private Boolean isActive;
    private Integer sortOrder;
    private Instant createdAt;
}