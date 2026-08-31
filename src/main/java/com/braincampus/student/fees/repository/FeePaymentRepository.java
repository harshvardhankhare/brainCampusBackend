package com.braincampus.student.fees.repository;

import com.braincampus.student.fees.entity.FeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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
}