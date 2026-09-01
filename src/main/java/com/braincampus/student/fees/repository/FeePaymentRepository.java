package com.braincampus.student.fees.repository;
import com.braincampus.student.fees.entity.FeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface FeePaymentRepository
        extends JpaRepository<FeePayment, Long> {

    List<FeePayment> findAllByTenantIdAndStudentFeeId(
            Long tenantId,
            Long studentFeeId
    );

    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM FeePayment p
            WHERE p.tenant.id = :tenantId
            AND p.studentFee.id = :studentFeeId
            """)
    BigDecimal findTotalPaidByTenantIdAndStudentFeeId(
            @Param("tenantId") Long tenantId,
            @Param("studentFeeId") Long studentFeeId
    );

    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM FeePayment p
            WHERE p.tenant.id = :tenantId
            AND p.paymentDate BETWEEN :startDate AND :endDate
            """)
    BigDecimal findTotalCollectedBetweenDates(
            @Param("tenantId") Long tenantId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM FeePayment p
            WHERE p.tenant.id = :tenantId
            AND p.paymentDate = :date
            """)
    BigDecimal findTotalCollectedOnDate(
            @Param("tenantId") Long tenantId,
            @Param("date") LocalDate date
    );


    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM FeePayment p
            WHERE p.tenant.id = :tenantId
            AND p.paymentDate BETWEEN :startDate AND :endDate
            """)
    BigDecimal findTotalCollectedForPeriod(
            @Param("tenantId") Long tenantId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    @Query("""
        SELECT p.paymentMethod, COALESCE(SUM(p.amount), 0)
        FROM FeePayment p
        WHERE p.tenant.id = :tenantId
        AND p.paymentDate BETWEEN :startDate AND :endDate
        GROUP BY p.paymentMethod
        """)
    List<Object[]> findCollectionByPaymentMethod(
            @Param("tenantId") Long tenantId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}