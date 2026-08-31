package com.braincampus.student.fees.controller;

import com.braincampus.common.dto.ApiResponse;
import com.braincampus.student.fees.dto.FeePaymentRequest;
import com.braincampus.student.fees.dto.FeePaymentResponse;
import com.braincampus.student.fees.service.FeePaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fee-payments")
@RequiredArgsConstructor
public class FeePaymentController {

    private final FeePaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_STUDENT')")
    public ResponseEntity<ApiResponse<FeePaymentResponse>> create(
            @Valid @RequestBody FeePaymentRequest request
    ) {

        FeePaymentResponse payment =
                paymentService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<FeePaymentResponse>builder()
                                .success(true)
                                .message("Fee payment recorded successfully")
                                .data(payment)
                                .build()
                );
    }

    @GetMapping("/fee/{feeId}")
    @PreAuthorize("hasAuthority('VIEW_STUDENT')")
    public ResponseEntity<ApiResponse<List<FeePaymentResponse>>> getPaymentsByFee(
            @PathVariable Long feeId
    ) {

        List<FeePaymentResponse> payments =
                paymentService.getPaymentsByFee(feeId);

        return ResponseEntity.ok(
                ApiResponse.<List<FeePaymentResponse>>builder()
                        .success(true)
                        .message("Fee payments fetched successfully")
                        .data(payments)
                        .build()
        );
    }
}