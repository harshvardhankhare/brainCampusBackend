package com.braincampus.auth.dto;
public record LoginResult(
        LoginResponse response,
        String refreshToken
) {
}
