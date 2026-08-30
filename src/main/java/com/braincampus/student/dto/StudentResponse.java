package com.braincampus.student.dto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class StudentResponse {

    private Long id;

    private String roleNumber;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private LocalDate dateOfBirth;

    private String address;

    private String parentName;

    private String parentPhone;

    private Boolean active;

    private String schoolCode;
}
