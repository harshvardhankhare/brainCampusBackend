package com.braincampus.management.dashboard.controller;
import com.braincampus.common.dto.ApiResponse;
import com.braincampus.management.dashboard.dto.DashboardResponse;
import com.braincampus.management.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/management/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_DASHBOARD')")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        DashboardResponse response =
                dashboardService.getDashboard(
                        startDate,
                        endDate
                );

        return ResponseEntity.ok(
                ApiResponse.<DashboardResponse>builder()
                        .success(true)
                        .message("Dashboard fetched successfully")
                        .data(response)
                        .build()
        );
    }
}