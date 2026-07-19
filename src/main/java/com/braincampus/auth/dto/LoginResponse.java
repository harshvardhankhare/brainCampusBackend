package com.braincampus.auth.dto;
import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class LoginResponse {

    private String accessToken;

    private String refreshToken;

    private Long userId;

    private String fullName;

    private String email;

    private String schoolCode;

    private String role;

    private Set<String> permissions;
}
