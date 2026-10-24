package com.braincampus.student.fees.controller;

import com.braincampus.common.dto.ApiResponse;
import com.braincampus.student.fees.dto.FeeTypeRequest;
import com.braincampus.student.fees.dto.FeeTypeResponse;
import com.braincampus.student.fees.dto.FeeTypeUpdateRequest;
import com.braincampus.student.fees.service.FeeTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fee-types")
@RequiredArgsConstructor
public class FeeTypeController {

    private final FeeTypeService feeTypeService;

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_STUDENT')")
    public ResponseEntity<ApiResponse<List<FeeTypeResponse>>> list(
            @RequestParam(defaultValue = "false") boolean includeInactive
    ) {
        return ResponseEntity.ok(ApiResponse.<List<FeeTypeResponse>>builder()
                .success(true)
                .message("Fee types fetched")
                .data(feeTypeService.list(includeInactive))
                .build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_STUDENT')")
    public ResponseEntity<ApiResponse<FeeTypeResponse>> create(
            @Valid @RequestBody FeeTypeRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<FeeTypeResponse>builder()
                        .success(true)
                        .message("Fee type created")
                        .data(feeTypeService.create(request))
                        .build());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('CREATE_STUDENT')")
    public ResponseEntity<ApiResponse<FeeTypeResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody FeeTypeUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.<FeeTypeResponse>builder()
                .success(true)
                .message("Fee type updated")
                .data(feeTypeService.update(id, request))
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CREATE_STUDENT')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        feeTypeService.delete(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Fee type deleted")
                .build());
    }
}