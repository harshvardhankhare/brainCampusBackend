package com.braincampus.management.dashboard.service;

import com.braincampus.management.dashboard.dto.DashboardExpenseSummary;
import com.braincampus.management.dashboard.dto.DashboardFeeSummary;
import com.braincampus.management.dashboard.dto.DashboardResponse;
import com.braincampus.management.dashboard.dto.DashboardSalarySummary;
import com.braincampus.management.repository.ExpenseRepository;
import com.braincampus.management.repository.SalaryPaymentRepository;
import com.braincampus.management.repository.SalaryStructureRepository;
import com.braincampus.security.SecurityUtils;
import com.braincampus.student.fees.entity.StudentFee;
import com.braincampus.student.fees.repository.FeePaymentRepository;
import com.braincampus.student.fees.repository.StudentFeeRepository;
import com.braincampus.student.repository.StudentRepository;
import com.braincampus.management.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;

    private final StudentFeeRepository studentFeeRepository;
    private final FeePaymentRepository feePaymentRepository;

    private final SalaryStructureRepository salaryStructureRepository;
    private final SalaryPaymentRepository salaryPaymentRepository;

    private final ExpenseRepository expenseRepository;

    public DashboardResponse getDashboard(
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date"
            );
        }

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        // =========================
        // STUDENTS
        // =========================

        long totalStudents =
                studentRepository
                        .findAllByTenantId(tenantId)
                        .stream()
                        .filter(s -> !s.getDeleted())
                        .count();

        // =========================
        // STAFF
        // =========================

        long totalStaff =
                staffRepository
                        .findAllByTenantId(tenantId)
                        .stream()
                        .filter(s -> !s.getDeleted())
                        .count();

        // =========================
        // FEES
        // =========================

        BigDecimal collected =
                feePaymentRepository
                        .findTotalCollectedBetweenDates(
                                tenantId,
                                startDate,
                                endDate
                        );

        if (collected == null) {
            collected = BigDecimal.ZERO;
        }

        List<StudentFee> fees =
                studentFeeRepository
                        .findAllByTenantId(tenantId)
                        .stream()
                        .filter(f -> !f.getDeleted())
                        .toList();

        BigDecimal totalFees =
                BigDecimal.ZERO;

        BigDecimal totalPaid =
                BigDecimal.ZERO;

        Set<Long> feeStudents =
                new HashSet<>();

        Set<Long> fullyPaidStudents =
                new HashSet<>();

        for (StudentFee fee : fees) {

            totalFees =
                    totalFees.add(
                            fee.getAmount()
                    );

            feeStudents.add(
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
        }

        /*
         * For dashboard collection:
         *
         * collected = actual payments in selected date range
         *
         * For overall fee balance:
         *
         * total fees - all payments
         */
        BigDecimal remaining =
                totalFees.subtract(totalPaid);

        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }

        /*
         * Determine students whose complete fee balance is zero.
         */
        for (Long studentId : feeStudents) {

            List<StudentFee> studentFees =
                    fees.stream()
                            .filter(f ->
                                    f.getStudent()
                                            .getId()
                                            .equals(studentId)
                            )
                            .toList();

            boolean fullyPaid = true;

            for (StudentFee fee : studentFees) {

                BigDecimal paid =
                        feePaymentRepository
                                .findTotalPaidByTenantIdAndStudentFeeId(
                                        tenantId,
                                        fee.getId()
                                );

                if (paid == null) {
                    paid = BigDecimal.ZERO;
                }

                if (paid.compareTo(
                        fee.getAmount()
                ) < 0) {

                    fullyPaid = false;
                    break;
                }
            }

            if (fullyPaid) {
                fullyPaidStudents.add(studentId);
            }
        }

        DashboardFeeSummary feeSummary =
                DashboardFeeSummary.builder()
                        .totalFees(totalFees)
                        .collected(collected)
                        .remaining(remaining)
                        .studentsPaid(
                                fullyPaidStudents.size()
                        )
                        .studentsPending(
                                feeStudents.size()
                                        - fullyPaidStudents.size()
                        )
                        .build();

        // =========================
        // EXPENSES
        // =========================

        BigDecimal totalExpenses =
                expenseRepository
                        .findTotalExpensesBetweenDates(
                                tenantId,
                                startDate,
                                endDate
                        );

        if (totalExpenses == null) {
            totalExpenses = BigDecimal.ZERO;
        }

        DashboardExpenseSummary expenseSummary =
                DashboardExpenseSummary.builder()
                        .totalExpenses(totalExpenses)
                        .build();

        // =========================
        // SALARY
        // =========================

        /*
         * For now salary payment is calculated
         * from actual salary payments in the period.
         */
        List<com.braincampus.management.entity.SalaryPayment>
                salaryPayments =
                salaryPaymentRepository
                        .findAllByTenantId(tenantId)
                        .stream()
                        .filter(p -> !p.getDeleted())
                        .filter(p ->
                                p.getPaymentDate() != null
                                        && !p.getPaymentDate()
                                        .isBefore(startDate)
                                        && !p.getPaymentDate()
                                        .isAfter(endDate)
                        )
                        .toList();

        BigDecimal salaryPaid =
                salaryPayments.stream()
                        .map(
                                com.braincampus.management.entity.SalaryPayment
                                        ::getAmount
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        long staffPaid =
                salaryPayments.stream()
                        .map(p -> p.getStaff().getId())
                        .distinct()
                        .count();

        long staffPending =
                Math.max(
                        totalStaff - staffPaid,
                        0
                );

        /*
         * Salary due will be calculated from
         * active salary structures.
         */
        BigDecimal salaryDue =
                salaryStructureRepository
                        .findAllByTenantId(tenantId)
                        .stream()
                        .filter(s -> !s.getDeleted())
                        .filter(s -> Boolean.TRUE.equals(
                                s.getActive()
                        ))
                        .map(s ->
                                s.getBasicSalary()
                                        .add(s.getAllowances())
                                        .subtract(s.getDeductions())
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal salaryPending =
                salaryDue.subtract(salaryPaid);

        if (salaryPending.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            salaryPending = BigDecimal.ZERO;
        }

        DashboardSalarySummary salarySummary =
                DashboardSalarySummary.builder()
                        .totalDue(salaryDue)
                        .totalPaid(salaryPaid)
                        .totalPending(salaryPending)
                        .staffCount(totalStaff)
                        .staffPaid(staffPaid)
                        .staffPending(staffPending)
                        .build();

        // =========================
        // FINANCIAL SUMMARY
        // =========================

        BigDecimal netAmount =
                collected
                        .subtract(totalExpenses)
                        .subtract(salaryPaid);

        return DashboardResponse.builder()
                .totalStudents(totalStudents)
                .totalStaff(totalStaff)
                .fees(feeSummary)
                .salary(salarySummary)
                .expenses(expenseSummary)
                .totalIncome(collected)
                .totalExpenses(
                        totalExpenses.add(salaryPaid)
                )
                .netAmount(netAmount)
                .build();
    }
}