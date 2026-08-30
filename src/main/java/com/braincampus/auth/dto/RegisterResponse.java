package com.braincampus.auth.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {

    private Long userId;

    private String schoolName;

    private String schoolCode;

    private String firstName;

    private String lastName;

    private String email;

    private String role;

    private String message;
}
