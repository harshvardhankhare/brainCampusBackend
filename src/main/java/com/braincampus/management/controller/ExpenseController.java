package com.braincampus.management.controller;
import com.braincampus.common.dto.ApiResponse;
import com.braincampus.management.dto.ExpenseRequest;
import com.braincampus.management.dto.ExpenseResponse;
import com.braincampus.management.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_EXPENSE')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> create(
            @Valid @RequestBody ExpenseRequest request
    ) {

        ExpenseResponse response =
                expenseService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<ExpenseResponse>builder()
                                .success(true)
                                .message("Expense created successfully")
                                .data(response)
                                .build()
                );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_EXPENSE')")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getAll() {

        List<ExpenseResponse> response =
                expenseService.getAll();

        return ResponseEntity.ok(
                ApiResponse.<List<ExpenseResponse>>builder()
                        .success(true)
                        .message("Expenses fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_EXPENSE')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getById(
            @PathVariable Long id
    ) {

        ExpenseResponse response =
                expenseService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<ExpenseResponse>builder()
                        .success(true)
                        .message("Expense fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasAuthority('VIEW_EXPENSE')")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getByCategory(
            @PathVariable String category
    ) {

        List<ExpenseResponse> response =
                expenseService.getByCategory(category);

        return ResponseEntity.ok(
                ApiResponse.<List<ExpenseResponse>>builder()
                        .success(true)
                        .message("Expenses fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAuthority('VIEW_EXPENSE')")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {

        List<ExpenseResponse> response =
                expenseService.getByDateRange(
                        startDate,
                        endDate
                );

        return ResponseEntity.ok(
                ApiResponse.<List<ExpenseResponse>>builder()
                        .success(true)
                        .message("Expenses fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_EXPENSE')")
    public ResponseEntity<ApiResponse<ExpenseResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request
    ) {

        ExpenseResponse response =
                expenseService.update(
                        id,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.<ExpenseResponse>builder()
                        .success(true)
                        .message("Expense updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_EXPENSE')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id
    ) {

        expenseService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Expense deleted successfully")
                        .data(null)
                        .build()
        );
    }
}