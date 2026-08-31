package com.braincampus.student.fees.repository;
import com.braincampus.student.fees.FeeType;
import com.braincampus.student.fees.entity.StudentFee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentFeeRepository
        extends JpaRepository<StudentFee, Long> {

    List<StudentFee> findAllByTenantIdAndStudentId(
            Long tenantId,
            Long studentId
    );

    List<StudentFee> findAllByTenantIdAndStudentIdAndAcademicYear(
            Long tenantId,
            Long studentId,
            String academicYear
    );

    Optional<StudentFee> findByIdAndTenantId(
            Long id,
            Long tenantId
    );

    Optional<StudentFee> findByTenantIdAndStudentIdAndAcademicYearAndFeeTypeAndFeeMonth(
            Long tenantId,
            Long studentId,
            String academicYear,
            FeeType feeType,
            Integer feeMonth
    );

    boolean existsByTenantIdAndStudentIdAndAcademicYearAndFeeTypeAndFeeMonth(
            Long tenantId,
            Long studentId,
            String academicYear,
            FeeType feeType,
            Integer feeMonth
    );
}