package com.braincampus.classSubject.controller;
import com.braincampus.classSubject.dto.ClassSubjectRequest;
import com.braincampus.classSubject.dto.ClassSubjectResponse;
import com.braincampus.classSubject.service.ClassSubjectService;
import com.braincampus.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/class-subjects")
@RequiredArgsConstructor
public class ClassSubjectController {

    private final ClassSubjectService classSubjectService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_CLASS_SUBJECT')")
    public ResponseEntity<ApiResponse<ClassSubjectResponse>> create(
            @Valid @RequestBody ClassSubjectRequest request
    ) {

        ClassSubjectResponse response =
                classSubjectService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<ClassSubjectResponse>builder()
                                .success(true)
                                .message("Subject assigned to class successfully")
                                .data(response)
                                .build()
                );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_CLASS_SUBJECT')")
    public ResponseEntity<ApiResponse<List<ClassSubjectResponse>>> getAll() {

        List<ClassSubjectResponse> response =
                classSubjectService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<ClassSubjectResponse>>builder()
                        .success(true)
                        .message("Class subjects fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_CLASS_SUBJECT')")
    public ResponseEntity<ApiResponse<ClassSubjectResponse>> getById(
            @PathVariable Long id
    ) {

        ClassSubjectResponse response =
                classSubjectService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<ClassSubjectResponse>builder()
                        .success(true)
                        .message("Class subject fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAuthority('VIEW_CLASS_SUBJECT')")
    public ResponseEntity<ApiResponse<List<ClassSubjectResponse>>> getByClass(
            @PathVariable Long classId
    ) {

        List<ClassSubjectResponse> response =
                classSubjectService.getByClass(classId);

        return ResponseEntity.ok(
                ApiResponse.<List<ClassSubjectResponse>>builder()
                        .success(true)
                        .message("Class subjects fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasAuthority('VIEW_CLASS_SUBJECT')")
    public ResponseEntity<ApiResponse<List<ClassSubjectResponse>>> getBySubject(
            @PathVariable Long subjectId
    ) {

        List<ClassSubjectResponse> response =
                classSubjectService.getBySubject(subjectId);

        return ResponseEntity.ok(
                ApiResponse.<List<ClassSubjectResponse>>builder()
                        .success(true)
                        .message("Subject classes fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_CLASS_SUBJECT')")
    public ResponseEntity<ApiResponse<ClassSubjectResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ClassSubjectRequest request
    ) {

        ClassSubjectResponse response =
                classSubjectService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<ClassSubjectResponse>builder()
                        .success(true)
                        .message("Class subject updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_CLASS_SUBJECT')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {

        classSubjectService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Class subject deleted successfully")
                        .data(null)
                        .build()
        );
    }
}