package com.braincampus.audit;
import com.braincampus.security.SecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10) // Runs after security filter so user/tenant context is populated
@RequiredArgsConstructor
public class ApiAuditLoggingFilter extends OncePerRequestFilter {

    private final ApiAuditService apiAuditService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Skip static resources or actuator health endpoints if needed
        String path = request.getRequestURI();
        if (path.startsWith("/actuator") || path.startsWith("/swagger") || path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();
        String errorMessage = null;

        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            errorMessage = ex.getMessage();
            throw ex; // Re-throw so standard exception handlers / error pages can handle it
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int statusCode = response.getStatus();

            // Extract Tenant & Username safely (even if unauthenticated)
            Long tenantId = null;
            String username = "ANONYMOUS";
            try {
                tenantId = SecurityUtils.getCurrentTenantId();
                if (SecurityUtils.getCurrentUser() != null) {
                    username = SecurityUtils.getCurrentUser().getUsername();
                }
            } catch (Exception ignored) {
                // Request might be unauthenticated (e.g., login, register)
            }

            ApiAuditLog auditLog = ApiAuditLog.builder()
                    .tenantId(tenantId)
                    .username(username)
                    .httpMethod(request.getMethod())
                    .endpoint(path)
                    .queryString(request.getQueryString())
                    .statusCode(statusCode)
                    .isSuccess(statusCode >= 200 && statusCode < 400)
                    .executionTimeMs(duration)
                    .clientIp(getClientIp(request))
                    .errorMessage(errorMessage)
                    .build();

            apiAuditService.recordLogAsync(auditLog);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}