package com.braincampus.exam.controller;

import com.braincampus.common.dto.ApiResponse;
import com.braincampus.exam.dto.ReportCardResponse;
import com.braincampus.exam.service.ReportCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report-cards")
@RequiredArgsConstructor
public class ReportCardController {

    private final ReportCardService reportCardService;

    @GetMapping("/student/{studentId}/exam/{examId}")
   // @PreAuthorize("hasAuthority('VIEW_REPORT_CARD')")
    public ResponseEntity<ApiResponse<ReportCardResponse>> getReportCard(
            @PathVariable Long studentId,
            @PathVariable Long examId
    ) {

        ReportCardResponse response =
                reportCardService.getReportCard(
                        studentId,
                        examId
                );

        return ResponseEntity.ok(
                ApiResponse.<ReportCardResponse>builder()
                        .success(true)
                        .message("Report card fetched successfully")
                        .data(response)
                        .build()
        );
    }
}