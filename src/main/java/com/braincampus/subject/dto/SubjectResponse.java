package com.braincampus.subject.dto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubjectResponse {

    private Long id;

    private String name;

    private String code;

    private Boolean active;
}