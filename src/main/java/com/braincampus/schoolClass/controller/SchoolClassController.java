package com.braincampus.schoolClass.controller;
import com.braincampus.common.dto.ApiResponse;
import com.braincampus.schoolClass.dto.SchoolClassRequest;
import com.braincampus.schoolClass.dto.SchoolClassResponse;
import com.braincampus.schoolClass.service.SchoolClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class SchoolClassController {

    private final SchoolClassService schoolClassService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_CLASS')")
    public ResponseEntity<ApiResponse<SchoolClassResponse>> create(
            @Valid @RequestBody SchoolClassRequest request
    ) {

        SchoolClassResponse response =
                schoolClassService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<SchoolClassResponse>builder()
                                .success(true)
                                .message("Class created successfully")
                                .data(response)
                                .build()
                );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_CLASS')")
    public ResponseEntity<ApiResponse<List<SchoolClassResponse>>> getAll() {

        List<SchoolClassResponse> classes =
                schoolClassService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<SchoolClassResponse>>builder()
                        .success(true)
                        .message("Classes fetched successfully")
                        .data(classes)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_CLASS')")
    public ResponseEntity<ApiResponse<SchoolClassResponse>> getById(
            @PathVariable Long id
    ) {

        SchoolClassResponse response =
                schoolClassService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<SchoolClassResponse>builder()
                        .success(true)
                        .message("Class fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_CLASS')")
    public ResponseEntity<ApiResponse<SchoolClassResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SchoolClassRequest request
    ) {

        SchoolClassResponse response =
                schoolClassService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<SchoolClassResponse>builder()
                        .success(true)
                        .message("Class updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_CLASS')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {

        schoolClassService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Class deleted successfully")
                        .build()
        );
    }
}