package com.braincampus.management.controller;
import com.braincampus.common.dto.ApiResponse;
import com.braincampus.management.dto.SalaryPaymentRequest;
import com.braincampus.management.dto.SalaryPaymentResponse;
import com.braincampus.management.service.SalaryPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/salary-payments")
@RequiredArgsConstructor
public class SalaryPaymentController {

    private final SalaryPaymentService salaryPaymentService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_SALARY_PAYMENT')")
    public ResponseEntity<ApiResponse<SalaryPaymentResponse>> create(
            @Valid @RequestBody SalaryPaymentRequest request
    ) {

        SalaryPaymentResponse response = salaryPaymentService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<SalaryPaymentResponse>builder()
                                .success(true)
                                .message("Salary payment created successfully")
                                .data(response)
                                .build()
                );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_SALARY_PAYMENT')")
    public ResponseEntity<ApiResponse<List<SalaryPaymentResponse>>> getAll() {

        List<SalaryPaymentResponse> response = salaryPaymentService.getAll();
        return ResponseEntity.ok(
                ApiResponse.<List<SalaryPaymentResponse>>builder()
                        .success(true)
                        .message("Salary payments fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_SALARY_PAYMENT')")
    public ResponseEntity<ApiResponse<SalaryPaymentResponse>> getById(@PathVariable Long id) {

        SalaryPaymentResponse response = salaryPaymentService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.<SalaryPaymentResponse>builder()
                        .success(true)
                        .message("Salary payment fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/staff/{staffId}")
    @PreAuthorize("hasAuthority('VIEW_SALARY_PAYMENT')")
    public ResponseEntity<ApiResponse<List<SalaryPaymentResponse>>> getByStaff(@PathVariable Long staffId) {

        List<SalaryPaymentResponse> response = salaryPaymentService.getByStaff(staffId);
        return ResponseEntity.ok(
                ApiResponse.<List<SalaryPaymentResponse>>builder()
                        .success(true)
                        .message("Staff salary payment history fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/month/{month}/year/{year}")
    @PreAuthorize("hasAuthority('VIEW_SALARY_PAYMENT')")
    public ResponseEntity<ApiResponse<List<SalaryPaymentResponse>>> getByMonth(
            @PathVariable Integer month,
            @PathVariable Integer year
    ) {

        List<SalaryPaymentResponse> response = salaryPaymentService.getByMonth(month, year);
        return ResponseEntity.ok(
                ApiResponse.<List<SalaryPaymentResponse>>builder()
                        .success(true)
                        .message("Monthly salary payments fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('VIEW_SALARY_PAYMENT')")
    public ResponseEntity<ApiResponse<List<SalaryPaymentResponse>>> getPending() {

        List<SalaryPaymentResponse> response = salaryPaymentService.getPending();
        return ResponseEntity.ok(
                ApiResponse.<List<SalaryPaymentResponse>>builder()
                        .success(true)
                        .message("Pending salary payments fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_SALARY_PAYMENT')")
    public ResponseEntity<ApiResponse<SalaryPaymentResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SalaryPaymentRequest request
    ) {

        SalaryPaymentResponse response = salaryPaymentService.update(id, request);
        return ResponseEntity.ok(
                ApiResponse.<SalaryPaymentResponse>builder()
                        .success(true)
                        .message("Salary payment updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_SALARY_PAYMENT')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        salaryPaymentService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Salary payment deleted successfully")
                        .data(null)
                        .build()
        );
    }
}