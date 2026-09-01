package com.braincampus.management.controller;

import com.braincampus.common.dto.ApiResponse;
import com.braincampus.management.dto.SalaryStructureRequest;
import com.braincampus.management.dto.SalaryStructureResponse;
import com.braincampus.management.service.SalaryStructureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salary-structures")
@RequiredArgsConstructor
public class SalaryStructureController {

    private final SalaryStructureService salaryStructureService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_SALARY_STRUCTURE')")
    public ResponseEntity<ApiResponse<SalaryStructureResponse>> create(
            @Valid @RequestBody SalaryStructureRequest request
    ) {

        SalaryStructureResponse response =
                salaryStructureService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<SalaryStructureResponse>builder()
                                .success(true)
                                .message("Salary structure created successfully")
                                .data(response)
                                .build()
                );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_SALARY_STRUCTURE')")
    public ResponseEntity<ApiResponse<List<SalaryStructureResponse>>> getAll() {

        List<SalaryStructureResponse> response =
                salaryStructureService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<SalaryStructureResponse>>builder()
                        .success(true)
                        .message("Salary structures fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_SALARY_STRUCTURE')")
    public ResponseEntity<ApiResponse<SalaryStructureResponse>> getById(
            @PathVariable Long id
    ) {

        SalaryStructureResponse response =
                salaryStructureService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<SalaryStructureResponse>builder()
                        .success(true)
                        .message("Salary structure fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/staff/{staffId}")
    @PreAuthorize("hasAuthority('VIEW_SALARY_STRUCTURE')")
    public ResponseEntity<ApiResponse<List<SalaryStructureResponse>>> getByStaff(
            @PathVariable Long staffId
    ) {

        List<SalaryStructureResponse> response =
                salaryStructureService.getByStaff(staffId);

        return ResponseEntity.ok(
                ApiResponse.<List<SalaryStructureResponse>>builder()
                        .success(true)
                        .message("Staff salary history fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/staff/{staffId}/current")
    @PreAuthorize("hasAuthority('VIEW_SALARY_STRUCTURE')")
    public ResponseEntity<ApiResponse<SalaryStructureResponse>> getCurrentSalary(
            @PathVariable Long staffId
    ) {

        SalaryStructureResponse response =
                salaryStructureService.getCurrentSalary(staffId);

        return ResponseEntity.ok(
                ApiResponse.<SalaryStructureResponse>builder()
                        .success(true)
                        .message("Current salary fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_SALARY_STRUCTURE')")
    public ResponseEntity<ApiResponse<SalaryStructureResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SalaryStructureRequest request
    ) {

        SalaryStructureResponse response =
                salaryStructureService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<SalaryStructureResponse>builder()
                        .success(true)
                        .message("Salary structure updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_SALARY_STRUCTURE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {

        salaryStructureService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Salary structure deleted successfully")
                        .data(null)
                        .build()
        );
    }
}