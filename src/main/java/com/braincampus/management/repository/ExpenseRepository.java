package com.braincampus.management.repository;

import com.braincampus.management.entity.Expense;
import com.braincampus.management.entity.ExpensePaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository
        extends JpaRepository<Expense, Long> {

    List<Expense> findAllByTenantId(Long tenantId);

    List<Expense> findAllByTenantIdAndCategory(
            Long tenantId,
            String category
    );

    Optional<Expense> findByIdAndTenantId(
            Long id,
            Long tenantId
    );

    List<Expense> findAllByTenantIdAndExpenseDateBetween(
            Long tenantId,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
            SELECT COALESCE(SUM(e.amount), 0)
            FROM Expense e
            WHERE e.tenant.id = :tenantId
            AND e.expenseDate BETWEEN :startDate AND :endDate
            """)
    BigDecimal findTotalExpensesBetweenDates(
            @Param("tenantId") Long tenantId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            SELECT e.category, COALESCE(SUM(e.amount), 0)
            FROM Expense e
            WHERE e.tenant.id = :tenantId
            AND e.expenseDate BETWEEN :startDate AND :endDate
            GROUP BY e.category
            """)
    List<Object[]> findExpensesByCategory(
            @Param("tenantId") Long tenantId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            SELECT e.paymentMethod, COALESCE(SUM(e.amount), 0)
            FROM Expense e
            WHERE e.tenant.id = :tenantId
            AND e.expenseDate BETWEEN :startDate AND :endDate
            GROUP BY e.paymentMethod
            """)
    List<Object[]> findExpensesByPaymentMethod(
            @Param("tenantId") Long tenantId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}