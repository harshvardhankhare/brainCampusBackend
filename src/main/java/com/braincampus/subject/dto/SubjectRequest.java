package com.braincampus.subject.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectRequest {

    @NotBlank(message = "Subject name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Subject code is required")
    @Size(max = 30)
    private String code;
}