package com.braincampus.student.fees.repository;
import com.braincampus.student.fees.FeeType;
import com.braincampus.student.fees.entity.StudentFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
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

    @Query("""
            SELECT COALESCE(SUM(sf.amount), 0)
            FROM StudentFee sf
            WHERE sf.tenant.id = :tenantId
            AND sf.academicYear = :academicYear
            """)
    BigDecimal findTotalFeesByAcademicYear(
            @Param("tenantId") Long tenantId,
            @Param("academicYear") String academicYear
    );


    @Query("""
            SELECT COALESCE(SUM(sf.amount), 0)
            FROM StudentFee sf
            WHERE sf.tenant.id = :tenantId
            AND sf.academicYear = :academicYear
            AND sf.student.schoolClass.id = :classId
            """)
    BigDecimal findTotalFeesByAcademicYearAndClass(
            @Param("tenantId") Long tenantId,
            @Param("academicYear") String academicYear,
            @Param("classId") Long classId
    );


    @Query("""
            SELECT COUNT(DISTINCT sf.student.id)
            FROM StudentFee sf
            WHERE sf.tenant.id = :tenantId
            AND sf.academicYear = :academicYear
            AND sf.student.schoolClass.id = :classId
            """)
    long countStudentsWithFeesByClass(
            @Param("tenantId") Long tenantId,
            @Param("academicYear") String academicYear,
            @Param("classId") Long classId
    );
    List<StudentFee> findAllByTenantIdAndAcademicYear(
            Long tenantId,
            String academicYear
    );
    @Query("""
        SELECT sf
        FROM StudentFee sf
        WHERE sf.tenant.id = :tenantId
        AND sf.academicYear = :academicYear
        AND sf.student.schoolClass.id = :classId
        """)
    List<StudentFee> findAllByTenantIdAndAcademicYearAndClassId(
            @Param("tenantId") Long tenantId,
            @Param("academicYear") String academicYear,
            @Param("classId") Long classId
    );
}