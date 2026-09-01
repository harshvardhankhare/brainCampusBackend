package com.braincampus.management.staff.controller;

import com.braincampus.common.dto.ApiResponse;
import com.braincampus.management.staff.dto.StaffRequest;
import com.braincampus.management.staff.dto.StaffResponse;
import com.braincampus.management.staff.entity.StaffType;
import com.braincampus.management.staff.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_STAFF')")
    public ResponseEntity<ApiResponse<StaffResponse>> create(@Valid @RequestBody StaffRequest request) {

        StaffResponse response = staffService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<StaffResponse>builder()
                                .success(true)
                                .message("Staff created successfully")
                                .data(response)
                                .build()
                );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_STAFF')")
    public ResponseEntity<ApiResponse<List<StaffResponse>>> getAll() {
        List<StaffResponse> response = staffService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<StaffResponse>>builder()
                        .success(true)
                        .message("Staff fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_STAFF')")
    public ResponseEntity<ApiResponse<StaffResponse>> getById(@PathVariable Long id) {

        StaffResponse response = staffService.getById(id);
        return ResponseEntity.ok(
                ApiResponse.<StaffResponse>builder()
                        .success(true)
                        .message("Staff fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/type/{type}")
    @PreAuthorize("hasAuthority('VIEW_STAFF')")
    public ResponseEntity<ApiResponse<List<StaffResponse>>> getByType(@PathVariable StaffType type) {

        List<StaffResponse> response = staffService.getByType(type);
        return ResponseEntity.ok(
                ApiResponse.<List<StaffResponse>>builder()
                        .success(true)
                        .message("Staff fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_STAFF')")
    public ResponseEntity<ApiResponse<StaffResponse>> update(@PathVariable Long id, @Valid @RequestBody StaffRequest request) {

        StaffResponse response = staffService.update(id, request);
        return ResponseEntity.ok(
                ApiResponse.<StaffResponse>builder()
                        .success(true)
                        .message("Staff updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_STAFF')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        staffService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Staff deleted successfully")
                        .data(null)
                        .build()
        );
    }
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('UPDATE_STAFF')")
    public ResponseEntity<ApiResponse<StaffResponse>> updateStatus(@PathVariable Long id, @RequestParam boolean active) {

        StaffResponse response = staffService.updateStatus(id, active);
        return ResponseEntity.ok(
                ApiResponse.<StaffResponse>builder()
                        .success(true)
                        .message(
                                active
                                        ? "Staff activated successfully"
                                        : "Staff deactivated successfully"
                        )
                        .data(response)
                        .build()
        );
    }
}