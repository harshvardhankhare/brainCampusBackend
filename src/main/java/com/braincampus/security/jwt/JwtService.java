package com.braincampus.security.jwt;
import com.braincampus.security.userDetails.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                jwtProperties.getSecret()
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateAccessToken(CustomUserDetails user) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("userId", user.getUserId());
        claims.put("tenantId", user.getTenantId());
        claims.put("schoolCode", user.getSchoolCode());
        claims.put("role", user.getUser().getRole().getName().name());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis()
                                + jwtProperties.getAccessTokenExpiration()))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {

        return extractClaims(token).getSubject();
    }

    public Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(
            String token,
            CustomUserDetails user
    ) {

        return extractUsername(token).equals(user.getUsername())
                && extractClaims(token)
                .getExpiration()
                .after(new Date());
    }
}
