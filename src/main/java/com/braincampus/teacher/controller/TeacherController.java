package com.braincampus.teacher.controller;
import com.braincampus.common.dto.ApiResponse;
import com.braincampus.teacher.dto.TeacherRequest;
import com.braincampus.teacher.dto.TeacherResponse;
import com.braincampus.teacher.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_TEACHER')")
    public ResponseEntity<ApiResponse<TeacherResponse>> create(
            @Valid @RequestBody TeacherRequest request
    ) {

        TeacherResponse response =
                teacherService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<TeacherResponse>builder()
                                .success(true)
                                .message("Teacher created successfully")
                                .data(response)
                                .build()
                );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_TEACHER')")
    public ResponseEntity<ApiResponse<List<TeacherResponse>>> getAll() {

        List<TeacherResponse> teachers =
                teacherService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<TeacherResponse>>builder()
                        .success(true)
                        .message("Teachers fetched successfully")
                        .data(teachers)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_TEACHER')")
    public ResponseEntity<ApiResponse<TeacherResponse>> getById(
            @PathVariable Long id
    ) {

        TeacherResponse response =
                teacherService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<TeacherResponse>builder()
                        .success(true)
                        .message("Teacher fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_TEACHER')")
    public ResponseEntity<ApiResponse<TeacherResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody TeacherRequest request
    ) {

        TeacherResponse response =
                teacherService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<TeacherResponse>builder()
                        .success(true)
                        .message("Teacher updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_TEACHER')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {

        teacherService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Teacher deleted successfully")
                        .data(null)
                        .build()
        );
    }
}