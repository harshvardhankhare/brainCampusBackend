package com.braincampus.management.staff.dto;
import com.braincampus.management.staff.entity.StaffType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class StaffResponse {

    private Long id;

    private String employeeCode;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private LocalDate dateOfBirth;

    private LocalDate joiningDate;

    private StaffType type;

    private String designation;

    private String address;

    private Boolean active;
}