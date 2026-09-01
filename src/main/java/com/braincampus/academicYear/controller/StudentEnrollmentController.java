package com.braincampus.academicYear.controller;

import com.braincampus.academicYear.dto.StudentEnrollmentRequest;
import com.braincampus.academicYear.dto.StudentEnrollmentResponse;
import com.braincampus.academicYear.service.StudentEnrollmentService;
import com.braincampus.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student-enrollments")
@RequiredArgsConstructor
public class StudentEnrollmentController {

    private final StudentEnrollmentService enrollmentService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_STUDENT_ENROLLMENT')")
    public ResponseEntity<ApiResponse<StudentEnrollmentResponse>> create(
            @Valid @RequestBody StudentEnrollmentRequest request
    ) {

        StudentEnrollmentResponse response =
                enrollmentService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<StudentEnrollmentResponse>builder()
                                .success(true)
                                .message("Student enrolled successfully")
                                .data(response)
                                .build()
                );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_STUDENT_ENROLLMENT')")
    public ResponseEntity<ApiResponse<List<StudentEnrollmentResponse>>> getAll() {

        List<StudentEnrollmentResponse> response =
                enrollmentService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<StudentEnrollmentResponse>>builder()
                        .success(true)
                        .message("Student enrollments fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_STUDENT_ENROLLMENT')")
    public ResponseEntity<ApiResponse<StudentEnrollmentResponse>> getById(
            @PathVariable Long id
    ) {

        StudentEnrollmentResponse response =
                enrollmentService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<StudentEnrollmentResponse>builder()
                        .success(true)
                        .message("Student enrollment fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAuthority('VIEW_STUDENT_ENROLLMENT')")
    public ResponseEntity<ApiResponse<List<StudentEnrollmentResponse>>> getByStudent(
            @PathVariable Long studentId
    ) {

        List<StudentEnrollmentResponse> response =
                enrollmentService.getByStudent(studentId);

        return ResponseEntity.ok(
                ApiResponse.<List<StudentEnrollmentResponse>>builder()
                        .success(true)
                        .message("Student enrollment history fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/academic-year/{academicYearId}")
    @PreAuthorize("hasAuthority('VIEW_STUDENT_ENROLLMENT')")
    public ResponseEntity<ApiResponse<List<StudentEnrollmentResponse>>> getByAcademicYear(
            @PathVariable Long academicYearId
    ) {

        List<StudentEnrollmentResponse> response =
                enrollmentService.getByAcademicYear(academicYearId);

        return ResponseEntity.ok(
                ApiResponse.<List<StudentEnrollmentResponse>>builder()
                        .success(true)
                        .message("Academic year enrollments fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/class/{classId}/academic-year/{academicYearId}")
    @PreAuthorize("hasAuthority('VIEW_STUDENT_ENROLLMENT')")
    public ResponseEntity<ApiResponse<List<StudentEnrollmentResponse>>> getByClass(
            @PathVariable Long classId,
            @PathVariable Long academicYearId
    ) {

        List<StudentEnrollmentResponse> response =
                enrollmentService.getByClass(
                        classId,
                        academicYearId
                );

        return ResponseEntity.ok(
                ApiResponse.<List<StudentEnrollmentResponse>>builder()
                        .success(true)
                        .message("Class enrollments fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_STUDENT_ENROLLMENT')")
    public ResponseEntity<ApiResponse<StudentEnrollmentResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody StudentEnrollmentRequest request
    ) {

        StudentEnrollmentResponse response =
                enrollmentService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<StudentEnrollmentResponse>builder()
                        .success(true)
                        .message("Student enrollment updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_STUDENT_ENROLLMENT')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {

        enrollmentService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Student enrollment deleted successfully")
                        .data(null)
                        .build()
        );
    }
}