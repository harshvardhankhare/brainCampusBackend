package com.braincampus.management.controller;
import com.braincampus.common.dto.ApiResponse;
import com.braincampus.management.dto.FeeCollectionResponse;
import com.braincampus.management.dto.FeeReportSummaryResponse;
import com.braincampus.management.dto.FeeStudentReportResponse;
import com.braincampus.management.service.FeeReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/management/reports/fees")
@RequiredArgsConstructor
public class FeeReportController {

    private final FeeReportService feeReportService;

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_FEE_REPORT')")
    public ResponseEntity<ApiResponse<FeeReportSummaryResponse>> getSummary(
            @RequestParam String academicYear,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Integer month
    ) {

        FeeReportSummaryResponse response =
                feeReportService.getSummary(
                        academicYear,
                        classId,
                        month
                );

        return ResponseEntity.ok(
                ApiResponse.<FeeReportSummaryResponse>builder()
                        .success(true)
                        .message("Fee report fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/students")
    @PreAuthorize("hasAuthority('VIEW_FEE_REPORT')")
    public ResponseEntity<ApiResponse<List<FeeStudentReportResponse>>> getStudentReport(
            @RequestParam String academicYear,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Integer month
    ) {

        List<FeeStudentReportResponse> response =
                feeReportService.getStudentReport(
                        academicYear,
                        classId,
                        month
                );

        return ResponseEntity.ok(
                ApiResponse.<List<FeeStudentReportResponse>>builder()
                        .success(true)
                        .message("Student fee report fetched successfully")
                        .data(response)
                        .build()
        );
    }
    @GetMapping("/collection")
    @PreAuthorize("hasAuthority('VIEW_FEE_REPORT')")
    public ResponseEntity<ApiResponse<FeeCollectionResponse>> getCollection(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {

        FeeCollectionResponse response =
                feeReportService.getCollection(
                        startDate,
                        endDate
                );

        return ResponseEntity.ok(
                ApiResponse.<FeeCollectionResponse>builder()
                        .success(true)
                        .message("Fee collection report fetched successfully")
                        .data(response)
                        .build()
        );
    }
}