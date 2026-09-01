package com.braincampus.exam.controller;
import com.braincampus.common.dto.ApiResponse;
import com.braincampus.exam.dto.ExamSubjectRequest;
import com.braincampus.exam.dto.ExamSubjectResponse;
import com.braincampus.exam.service.ExamSubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exam-subjects")
@RequiredArgsConstructor
public class ExamSubjectController {

    private final ExamSubjectService examSubjectService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_EXAM_SUBJECT')")
    public ResponseEntity<ApiResponse<ExamSubjectResponse>> create(
            @Valid @RequestBody ExamSubjectRequest request
    ) {

        ExamSubjectResponse response =
                examSubjectService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<ExamSubjectResponse>builder()
                                .success(true)
                                .message("Subject added to exam successfully")
                                .data(response)
                                .build()
                );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_EXAM_SUBJECT')")
    public ResponseEntity<ApiResponse<List<ExamSubjectResponse>>> getAll() {

        List<ExamSubjectResponse> response =
                examSubjectService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<ExamSubjectResponse>>builder()
                        .success(true)
                        .message("Exam subjects fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_EXAM_SUBJECT')")
    public ResponseEntity<ApiResponse<ExamSubjectResponse>> getById(
            @PathVariable Long id
    ) {

        ExamSubjectResponse response =
                examSubjectService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<ExamSubjectResponse>builder()
                        .success(true)
                        .message("Exam subject fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/exam/{examId}")
    @PreAuthorize("hasAuthority('VIEW_EXAM_SUBJECT')")
    public ResponseEntity<ApiResponse<List<ExamSubjectResponse>>> getByExam(
            @PathVariable Long examId
    ) {

        List<ExamSubjectResponse> response =
                examSubjectService.getByExam(examId);

        return ResponseEntity.ok(
                ApiResponse.<List<ExamSubjectResponse>>builder()
                        .success(true)
                        .message("Exam subjects fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_EXAM_SUBJECT')")
    public ResponseEntity<ApiResponse<ExamSubjectResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ExamSubjectRequest request
    ) {

        ExamSubjectResponse response =
                examSubjectService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<ExamSubjectResponse>builder()
                        .success(true)
                        .message("Exam subject updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_EXAM_SUBJECT')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {

        examSubjectService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Exam subject deleted successfully")
                        .data(null)
                        .build()
        );
    }
}