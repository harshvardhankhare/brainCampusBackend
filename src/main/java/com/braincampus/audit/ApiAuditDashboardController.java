package com.braincampus.audit;
import com.braincampus.common.dto.ApiResponse;
import com.braincampus.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.braincampus.common.dto.PageResponse;
@RestController
@RequestMapping("/api/audit/dashboard")
@RequiredArgsConstructor
public class ApiAuditDashboardController {

    private final ApiAuditLogRepository auditLogRepository;

    @GetMapping("/status-distribution")
    @PreAuthorize("hasAuthority('VIEW_DASHBOARD')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getStatusDistribution(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate
    ) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(7); // Default to last 7 days
        }

        List<Map<String, Object>> data = auditLogRepository.getStatusCodeDistribution(tenantId, startDate);
        return ResponseEntity.ok(ApiResponse.<List<Map<String, Object>>>builder()
                .success(true)
                .data(data)
                .build());
    }

    @GetMapping("/endpoint-latency")
    @PreAuthorize("hasAuthority('VIEW_DASHBOARD')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getEndpointLatency(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate
    ) {
        Long tenantId = SecurityUtils.getCurrentTenantId();
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(7);
        }

        List<Map<String, Object>> data = auditLogRepository.getEndpointLatencyStats(tenantId, startDate);
        return ResponseEntity.ok(ApiResponse.<List<Map<String, Object>>>builder()
                .success(true)
                .data(data)
                .build());
    }

    @GetMapping("/logs")
    @PreAuthorize("hasAuthority('VIEW_DASHBOARD')")
    public ResponseEntity<ApiResponse<PageResponse<ApiAuditLog>>> getAuditLogs(
            @RequestParam(required = false) Boolean success,
            @RequestParam(required = false) String search,
            @PageableDefault(page = 0, size = 15, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long tenantId = SecurityUtils.getCurrentTenantId();

        String normalizedSearch = (search != null && !search.isBlank()) ? search.trim() : null;

        Page<ApiAuditLog> logs = auditLogRepository.findLogsWithFilters(tenantId, success, normalizedSearch, pageable);

        return ResponseEntity.ok(ApiResponse.<PageResponse<ApiAuditLog>>builder()
                .success(true)
                .data(PageResponse.from(logs))
                .build());
    }
}