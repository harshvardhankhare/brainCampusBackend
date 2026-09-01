package com.braincampus.management.service;

import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.management.dto.ExpenseRequest;
import com.braincampus.management.dto.ExpenseResponse;
import com.braincampus.management.entity.Expense;
import com.braincampus.management.repository.ExpenseRepository;
import com.braincampus.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseResponse create(ExpenseRequest request) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        Expense expense = Expense.builder()
                .category(request.getCategory())
                .amount(request.getAmount())
                .expenseDate(request.getExpenseDate())
                .description(request.getDescription())
                .paymentMethod(request.getPaymentMethod())
                .referenceNumber(request.getReferenceNumber())
                .tenant(
                        SecurityUtils
                                .getCurrentUser()
                                .getUser()
                                .getTenant()
                )
                .build();

        expense = expenseRepository.save(expense);

        return mapToResponse(expense);
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> getAll() {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        return expenseRepository
                .findAllByTenantId(tenantId)
                .stream()
                .filter(e -> !e.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExpenseResponse getById(Long id) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        Expense expense =
                expenseRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(e -> !e.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Expense not found"
                                )
                        );

        return mapToResponse(expense);
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> getByCategory(
            String category
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        return expenseRepository
                .findAllByTenantIdAndCategory(
                        tenantId,
                        category
                )
                .stream()
                .filter(e -> !e.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> getByDateRange(
            java.time.LocalDate startDate,
            java.time.LocalDate endDate
    ) {

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date"
            );
        }

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        return expenseRepository
                .findAllByTenantIdAndExpenseDateBetween(
                        tenantId,
                        startDate,
                        endDate
                )
                .stream()
                .filter(e -> !e.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    public ExpenseResponse update(
            Long id,
            ExpenseRequest request
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        Expense expense =
                expenseRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(e -> !e.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Expense not found"
                                )
                        );

        expense.setCategory(
                request.getCategory()
        );

        expense.setAmount(
                request.getAmount()
        );

        expense.setExpenseDate(
                request.getExpenseDate()
        );

        expense.setDescription(
                request.getDescription()
        );

        expense.setPaymentMethod(
                request.getPaymentMethod()
        );

        expense.setReferenceNumber(
                request.getReferenceNumber()
        );

        return mapToResponse(
                expenseRepository.save(expense)
        );
    }

    public void delete(Long id) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        Expense expense =
                expenseRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(e -> !e.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Expense not found"
                                )
                        );

        expense.setDeleted(true);

        expenseRepository.save(expense);
    }

    private ExpenseResponse mapToResponse(
            Expense expense
    ) {

        return ExpenseResponse.builder()
                .id(expense.getId())
                .category(expense.getCategory())
                .amount(expense.getAmount())
                .expenseDate(expense.getExpenseDate())
                .description(expense.getDescription())
                .paymentMethod(expense.getPaymentMethod())
                .referenceNumber(
                        expense.getReferenceNumber()
                )
                .build();
    }
}