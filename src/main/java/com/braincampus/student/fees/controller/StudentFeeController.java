package com.braincampus.student.fees.controller;

import com.braincampus.common.dto.ApiResponse;
import com.braincampus.student.fees.dto.StudentFeeRequest;
import com.braincampus.student.fees.dto.StudentFeeResponse;
import com.braincampus.student.fees.service.StudentFeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student-fees")
@RequiredArgsConstructor
public class StudentFeeController {

    private final StudentFeeService feeService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_STUDENT')")
    public ResponseEntity<ApiResponse<StudentFeeResponse>> create(
            @Valid @RequestBody StudentFeeRequest request
    ) {

        StudentFeeResponse fee =
                feeService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<StudentFeeResponse>builder()
                                .success(true)
                                .message("Fee created successfully")
                                .data(fee)
                                .build()
                );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_STUDENT')")
    public ResponseEntity<ApiResponse<StudentFeeResponse>> getById(
            @PathVariable Long id
    ) {

        StudentFeeResponse fee =
                feeService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<StudentFeeResponse>builder()
                        .success(true)
                        .message("Fee fetched successfully")
                        .data(fee)
                        .build()
        );
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAuthority('VIEW_STUDENT')")
    public ResponseEntity<ApiResponse<List<StudentFeeResponse>>> getStudentFees(
            @PathVariable Long studentId
    ) {

        List<StudentFeeResponse> fees =
                feeService.getStudentFees(studentId);

        return ResponseEntity.ok(
                ApiResponse.<List<StudentFeeResponse>>builder()
                        .success(true)
                        .message("Student fees fetched successfully")
                        .data(fees)
                        .build()
        );
    }

    @GetMapping("/student/{studentId}/academic-year")
    @PreAuthorize("hasAuthority('VIEW_STUDENT')")
    public ResponseEntity<ApiResponse<List<StudentFeeResponse>>> getStudentFeesByAcademicYear(
            @PathVariable Long studentId,
            @RequestParam String academicYear
    ) {

        List<StudentFeeResponse> fees =
                feeService.getStudentFeesByAcademicYear(
                        studentId,
                        academicYear
                );

        return ResponseEntity.ok(
                ApiResponse.<List<StudentFeeResponse>>builder()
                        .success(true)
                        .message("Student fees fetched successfully")
                        .data(fees)
                        .build()
        );
    }
}