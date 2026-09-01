package com.braincampus.academicYear.controller;

import com.braincampus.academicYear.dto.AcademicYearRequest;
import com.braincampus.academicYear.dto.AcademicYearResponse;
import com.braincampus.academicYear.service.AcademicYearService;
import com.braincampus.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/academic-years")
@RequiredArgsConstructor
public class AcademicYearController {

    private final AcademicYearService academicYearService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_ACADEMIC_YEAR')")
    public ResponseEntity<ApiResponse<AcademicYearResponse>> create(
            @Valid @RequestBody AcademicYearRequest request
    ) {

        AcademicYearResponse response =
                academicYearService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<AcademicYearResponse>builder()
                                .success(true)
                                .message("Academic year created successfully")
                                .data(response)
                                .build()
                );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_ACADEMIC_YEAR')")
    public ResponseEntity<ApiResponse<List<AcademicYearResponse>>> getAll() {

        List<AcademicYearResponse> response =
                academicYearService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<AcademicYearResponse>>builder()
                        .success(true)
                        .message("Academic years fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_ACADEMIC_YEAR')")
    public ResponseEntity<ApiResponse<AcademicYearResponse>> getById(
            @PathVariable Long id
    ) {

        AcademicYearResponse response =
                academicYearService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<AcademicYearResponse>builder()
                        .success(true)
                        .message("Academic year fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('VIEW_ACADEMIC_YEAR')")
    public ResponseEntity<ApiResponse<AcademicYearResponse>> getActive() {

        AcademicYearResponse response =
                academicYearService.getActive();

        return ResponseEntity.ok(
                ApiResponse.<AcademicYearResponse>builder()
                        .success(true)
                        .message("Active academic year fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_ACADEMIC_YEAR')")
    public ResponseEntity<ApiResponse<AcademicYearResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody AcademicYearRequest request
    ) {

        AcademicYearResponse response =
                academicYearService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<AcademicYearResponse>builder()
                        .success(true)
                        .message("Academic year updated successfully")
                        .data(response)
                        .build()
        );
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('ACTIVATE_ACADEMIC_YEAR')")
    public ResponseEntity<ApiResponse<AcademicYearResponse>> activate(
            @PathVariable Long id
    ) {

        AcademicYearResponse response =
                academicYearService.activate(id);

        return ResponseEntity.ok(
                ApiResponse.<AcademicYearResponse>builder()
                        .success(true)
                        .message("Academic year activated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_ACADEMIC_YEAR')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {

        academicYearService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Academic year deleted successfully")
                        .data(null)
                        .build()
        );
    }
}