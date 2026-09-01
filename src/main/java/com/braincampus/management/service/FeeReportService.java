package com.braincampus.management.service;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.management.dto.FeeCollectionResponse;
import com.braincampus.management.dto.FeeReportSummaryResponse;
import com.braincampus.management.dto.FeeStudentReportResponse;
import com.braincampus.schoolClass.entity.SchoolClass;
import com.braincampus.schoolClass.repository.SchoolClassRepository;
import com.braincampus.security.SecurityUtils;
import com.braincampus.student.entity.Student;
import com.braincampus.student.fees.entity.FeePayment;
import com.braincampus.student.fees.entity.StudentFee;
import com.braincampus.student.fees.repository.FeePaymentRepository;
import com.braincampus.student.fees.repository.StudentFeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeeReportService {

    private final StudentFeeRepository studentFeeRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final SchoolClassRepository schoolClassRepository;

    public FeeReportSummaryResponse getSummary(
            String academicYear,
            Long classId,
            Integer month
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        List<StudentFee> fees =
                getFilteredFees(
                        tenantId,
                        academicYear,
                        classId,
                        month
                );

        BigDecimal totalFees = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;

        Set<Long> students = new HashSet<>();
        Set<Long> paidStudents = new HashSet<>();

        for (StudentFee fee : fees) {

            if (fee.getDeleted()) {
                continue;
            }

            totalFees =
                    totalFees.add(fee.getAmount());

            students.add(
                    fee.getStudent().getId()
            );

            BigDecimal paid =
                    feePaymentRepository
                            .findTotalPaidByTenantIdAndStudentFeeId(
                                    tenantId,
                                    fee.getId()
                            );

            if (paid == null) {
                paid = BigDecimal.ZERO;
            }

            totalPaid =
                    totalPaid.add(paid);

            if (paid.compareTo(BigDecimal.ZERO) > 0) {
                paidStudents.add(
                        fee.getStudent().getId()
                );
            }
        }

        BigDecimal remaining =
                totalFees.subtract(totalPaid);

        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }

        return FeeReportSummaryResponse.builder()
                .totalFees(totalFees)
                .totalCollected(totalPaid)
                .totalRemaining(remaining)
                .totalStudents(students.size())
                .studentsPaid(paidStudents.size())
                .studentsPending(
                        students.size()
                                - paidStudents.size()
                )
                .build();
    }

    public FeeCollectionResponse getCollection(
            LocalDate startDate,
            LocalDate endDate
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date"
            );
        }

        BigDecimal totalCollected =
                feePaymentRepository
                        .findTotalCollectedBetweenDates(
                                tenantId,
                                startDate,
                                endDate
                        );

        List<Object[]> rows =
                feePaymentRepository
                        .findCollectionByPaymentMethod(
                                tenantId,
                                startDate,
                                endDate
                        );

        Map<String, BigDecimal> byPaymentMethod =
                new LinkedHashMap<>();

        for (Object[] row : rows) {

            String method =
                    row[0].toString();

            BigDecimal amount =
                    (BigDecimal) row[1];

            byPaymentMethod.put(
                    method,
                    amount
            );
        }

        return FeeCollectionResponse.builder()
                .totalCollected(totalCollected)
                .collectionByPaymentMethod(byPaymentMethod)
                .startDate(startDate.toString())
                .endDate(endDate.toString())
                .build();
    }
    public List<FeeStudentReportResponse> getStudentReport(
            String academicYear,
            Long classId,
            Integer month
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        List<StudentFee> fees =
                getFilteredFees(
                        tenantId,
                        academicYear,
                        classId,
                        month
                );

        /*
         * Group all fees belonging to the same student.
         *
         * A student can have:
         * Tuition fee
         * Transport fee
         * Exam fee
         * etc.
         */
        Map<Long, StudentFeeSummary> studentMap =
                new LinkedHashMap<>();

        for (StudentFee fee : fees) {

            if (fee.getDeleted()) {
                continue;
            }

            Student student =
                    fee.getStudent();

            Long studentId =
                    student.getId();

            StudentFeeSummary summary =
                    studentMap.computeIfAbsent(
                            studentId,
                            id -> new StudentFeeSummary(student)
                    );

            summary.totalFees =
                    summary.totalFees.add(
                            fee.getAmount()
                    );

            BigDecimal paid =
                    feePaymentRepository
                            .findTotalPaidByTenantIdAndStudentFeeId(
                                    tenantId,
                                    fee.getId()
                            );

            if (paid == null) {
                paid = BigDecimal.ZERO;
            }

            summary.totalPaid =
                    summary.totalPaid.add(paid);
        }

        List<FeeStudentReportResponse> result =
                new ArrayList<>();

        for (StudentFeeSummary summary :
                studentMap.values()) {

            BigDecimal remaining =
                    summary.totalFees
                            .subtract(summary.totalPaid);

            if (remaining.compareTo(BigDecimal.ZERO) < 0) {
                remaining = BigDecimal.ZERO;
            }

            Student student =
                    summary.student;

            SchoolClass schoolClass =
                    student.getSchoolClass();

            result.add(
                    FeeStudentReportResponse.builder()
                            .studentId(student.getId())
                            .roleNumber(student.getRoleNumber())
                            .studentName(
                                    student.getFirstName()
                                            + " "
                                            + (
                                            student.getLastName() != null
                                                    ? student.getLastName()
                                                    : ""
                                    )
                            )
                            .classId(
                                    schoolClass.getId()
                            )
                            .className(
                                    schoolClass.getName()
                            )
                            .section(
                                    schoolClass.getSection()
                            )
                            .totalFees(
                                    summary.totalFees
                            )
                            .totalPaid(
                                    summary.totalPaid
                            )
                            .remaining(
                                    remaining
                            )
                            .paid(
                                    remaining.compareTo(
                                            BigDecimal.ZERO
                                    ) == 0
                            )
                            .build()
            );
        }

        return result;
    }

    private List<StudentFee> getFilteredFees(
            Long tenantId,
            String academicYear,
            Long classId,
            Integer month
    ) {

        List<StudentFee> fees;

        if (classId == null) {

            fees =
                    studentFeeRepository
                            .findAllByTenantIdAndAcademicYear(
                                    tenantId,
                                    academicYear
                            );

        } else {

            /*
             * Make sure the class belongs to this tenant.
             */
            schoolClassRepository
                    .findByIdAndTenantId(
                            classId,
                            tenantId
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Class not found"
                            )
                    );

            fees =
                    studentFeeRepository
                            .findAllByTenantIdAndAcademicYearAndClassId(
                                    tenantId,
                                    academicYear,
                                    classId
                            );
        }

        if (month != null) {

            fees =
                    fees.stream()
                            .filter(fee ->
                                    Objects.equals(
                                            fee.getFeeMonth(),
                                            month
                                    )
                            )
                            .toList();
        }

        return fees;
    }

    private static class StudentFeeSummary {

        private final Student student;

        private BigDecimal totalFees =
                BigDecimal.ZERO;

        private BigDecimal totalPaid =
                BigDecimal.ZERO;

        private StudentFeeSummary(Student student) {
            this.student = student;
        }
    }
}