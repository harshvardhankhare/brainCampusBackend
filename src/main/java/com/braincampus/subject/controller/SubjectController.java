package com.braincampus.subject.controller;
import com.braincampus.common.dto.ApiResponse;
import com.braincampus.subject.dto.SubjectRequest;
import com.braincampus.subject.dto.SubjectResponse;
import com.braincampus.subject.service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_SUBJECT')")
    public ResponseEntity<ApiResponse<SubjectResponse>> create(
            @Valid @RequestBody SubjectRequest request
    ) {

        SubjectResponse response =
                subjectService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<SubjectResponse>builder()
                                .success(true)
                                .message("Subject created successfully")
                                .data(response)
                                .build()
                );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_SUBJECT')")
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> getAll() {

        List<SubjectResponse> subjects =
                subjectService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<SubjectResponse>>builder()
                        .success(true)
                        .message("Subjects fetched successfully")
                        .data(subjects)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_SUBJECT')")
    public ResponseEntity<ApiResponse<SubjectResponse>> getById(
            @PathVariable Long id
    ) {

        SubjectResponse response =
                subjectService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<SubjectResponse>builder()
                        .success(true)
                        .message("Subject fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_SUBJECT')")
    public ResponseEntity<ApiResponse<SubjectResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SubjectRequest request
    ) {

        SubjectResponse response =
                subjectService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<SubjectResponse>builder()
                        .success(true)
                        .message("Subject updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_SUBJECT')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {

        subjectService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Subject deleted successfully")
                        .build()
        );
    }
}