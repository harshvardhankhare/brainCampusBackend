package com.braincampus.exam.controller;
import com.braincampus.common.dto.ApiResponse;
import com.braincampus.exam.dto.StudentResultRequest;
import com.braincampus.exam.dto.StudentResultResponse;
import com.braincampus.exam.service.StudentResultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student-results")
@RequiredArgsConstructor
public class StudentResultController {

    private final StudentResultService studentResultService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_STUDENT_RESULT')")
    public ResponseEntity<ApiResponse<StudentResultResponse>> create(
            @Valid @RequestBody StudentResultRequest request
    ) {

        StudentResultResponse response =
                studentResultService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<StudentResultResponse>builder()
                                .success(true)
                                .message("Student result created successfully")
                                .data(response)
                                .build()
                );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_STUDENT_RESULT')")
    public ResponseEntity<ApiResponse<List<StudentResultResponse>>> getAll() {

        List<StudentResultResponse> response =
                studentResultService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<StudentResultResponse>>builder()
                        .success(true)
                        .message("Student results fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_STUDENT_RESULT')")
    public ResponseEntity<ApiResponse<StudentResultResponse>> getById(
            @PathVariable Long id
    ) {

        StudentResultResponse response =
                studentResultService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<StudentResultResponse>builder()
                        .success(true)
                        .message("Student result fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAuthority('VIEW_STUDENT_RESULT')")
    public ResponseEntity<ApiResponse<List<StudentResultResponse>>> getByStudent(
            @PathVariable Long studentId
    ) {

        List<StudentResultResponse> response =
                studentResultService.getByStudent(studentId);

        return ResponseEntity.ok(
                ApiResponse.<List<StudentResultResponse>>builder()
                        .success(true)
                        .message("Student results fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/exam-subject/{examSubjectId}")
    @PreAuthorize("hasAuthority('VIEW_STUDENT_RESULT')")
    public ResponseEntity<ApiResponse<List<StudentResultResponse>>> getByExamSubject(
            @PathVariable Long examSubjectId
    ) {

        List<StudentResultResponse> response =
                studentResultService.getByExamSubject(examSubjectId);

        return ResponseEntity.ok(
                ApiResponse.<List<StudentResultResponse>>builder()
                        .success(true)
                        .message("Exam subject results fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_STUDENT_RESULT')")
    public ResponseEntity<ApiResponse<StudentResultResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody StudentResultRequest request
    ) {

        StudentResultResponse response =
                studentResultService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<StudentResultResponse>builder()
                        .success(true)
                        .message("Student result updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_STUDENT_RESULT')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {

        studentResultService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Student result deleted successfully")
                        .data(null)
                        .build()
        );
    }
}