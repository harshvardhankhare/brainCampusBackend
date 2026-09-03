package com.braincampus.exam.controller;
import com.braincampus.common.dto.ApiResponse;
import com.braincampus.exam.dto.ExamRequest;
import com.braincampus.exam.dto.ExamResponse;
import com.braincampus.exam.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_EXAM')")
    public ResponseEntity<ApiResponse<ExamResponse>> create(
            @Valid @RequestBody ExamRequest request
    ) {

        ExamResponse response =
                examService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<ExamResponse>builder()
                                .success(true)
                                .message("Exam created successfully")
                                .data(response)
                                .build()
                );
    }

    @GetMapping
    //@PreAuthorize("hasAuthority('VIEW_EXAM')")
    public ResponseEntity<ApiResponse<List<ExamResponse>>> getAll() {

        List<ExamResponse> response =
                examService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<ExamResponse>>builder()
                        .success(true)
                        .message("Exams fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
   // @PreAuthorize("hasAuthority('VIEW_EXAM')")
    public ResponseEntity<ApiResponse<ExamResponse>> getById(
            @PathVariable Long id
    ) {

        ExamResponse response =
                examService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<ExamResponse>builder()
                        .success(true)
                        .message("Exam fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/class/{classId}")
   // @PreAuthorize("hasAuthority('VIEW_EXAM')")
    public ResponseEntity<ApiResponse<List<ExamResponse>>> getByClass(
            @PathVariable Long classId
    ) {

        List<ExamResponse> response =
                examService.getByClass(classId);

        return ResponseEntity.ok(
                ApiResponse.<List<ExamResponse>>builder()
                        .success(true)
                        .message("Class exams fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_EXAM')")
    public ResponseEntity<ApiResponse<ExamResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ExamRequest request
    ) {

        ExamResponse response =
                examService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<ExamResponse>builder()
                        .success(true)
                        .message("Exam updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_EXAM')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {

        examService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Exam deleted successfully")
                        .data(null)
                        .build()
        );
    }
}