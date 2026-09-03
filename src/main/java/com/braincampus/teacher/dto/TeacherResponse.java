package com.braincampus.teacher.dto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeacherResponse {

    private Long id;

    private String employeeCode;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private Boolean active;
}