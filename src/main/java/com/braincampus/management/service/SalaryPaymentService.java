package com.braincampus.management.service;

import com.braincampus.exception.DuplicateResourceException;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.management.dto.SalaryPaymentRequest;
import com.braincampus.management.dto.SalaryPaymentResponse;
import com.braincampus.management.entity.SalaryPayment;
import com.braincampus.management.repository.SalaryPaymentRepository;
import com.braincampus.management.repository.SalaryStructureRepository;
import com.braincampus.management.entity.SalaryStructure;
import com.braincampus.management.staff.entity.Staff;
import com.braincampus.management.staff.repository.StaffRepository;
import com.braincampus.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SalaryPaymentService {

    private final SalaryPaymentRepository salaryPaymentRepository;
    private final SalaryStructureRepository salaryStructureRepository;
    private final StaffRepository staffRepository;

    public SalaryPaymentResponse create(
            SalaryPaymentRequest request
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        Staff staff =
                staffRepository
                        .findByIdAndTenantId(
                                request.getStaffId(),
                                tenantId
                        )
                        .filter(s -> !s.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Staff not found"
                                )
                        );

        if (salaryPaymentRepository
                .existsByStaffIdAndMonthAndYearAndTenantId(
                        staff.getId(),
                        request.getMonth(),
                        request.getYear(),
                        tenantId
                )) {

            throw new DuplicateResourceException(
                    "Salary payment already exists for this staff member and month"
            );
        }

        if (request.getStatus()
                == com.braincampus.management.entity.SalaryPaymentStatus.PAID
                && request.getPaymentDate() == null) {

            throw new IllegalArgumentException(
                    "Payment date is required when salary is marked as PAID"
            );
        }

        if (request.getStatus()
                == com.braincampus.management.entity.SalaryPaymentStatus.PENDING
                && request.getAmount()
                .compareTo(BigDecimal.ZERO) != 0) {

            throw new IllegalArgumentException(
                    "Pending salary payment must have amount 0"
            );
        }

        SalaryPayment payment =
                SalaryPayment.builder()
                        .staff(staff)
                        .month(request.getMonth())
                        .year(request.getYear())
                        .amount(request.getAmount())
                        .paymentDate(request.getPaymentDate())
                        .status(request.getStatus())
                        .remarks(request.getRemarks())
                        .tenant(
                                SecurityUtils
                                        .getCurrentUser()
                                        .getUser()
                                        .getTenant()
                        )
                        .build();

        payment =
                salaryPaymentRepository.save(payment);

        return mapToResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<SalaryPaymentResponse> getAll() {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        return salaryPaymentRepository
                .findAllByTenantId(tenantId)
                .stream()
                .filter(p -> !p.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SalaryPaymentResponse getById(
            Long id
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        SalaryPayment payment =
                salaryPaymentRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(p -> !p.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Salary payment not found"
                                )
                        );

        return mapToResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<SalaryPaymentResponse> getByStaff(
            Long staffId
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        staffRepository
                .findByIdAndTenantId(
                        staffId,
                        tenantId
                )
                .filter(s -> !s.getDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Staff not found"
                        )
                );

        return salaryPaymentRepository
                .findAllByStaffIdAndTenantId(
                        staffId,
                        tenantId
                )
                .stream()
                .filter(p -> !p.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SalaryPaymentResponse> getByMonth(
            Integer month,
            Integer year
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        return salaryPaymentRepository
                .findAllByMonthAndYearAndTenantId(
                        month,
                        year,
                        tenantId
                )
                .stream()
                .filter(p -> !p.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SalaryPaymentResponse> getPending() {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        return salaryPaymentRepository
                .findAllByStatusAndTenantId(
                        com.braincampus.management.entity.SalaryPaymentStatus.PENDING,
                        tenantId
                )
                .stream()
                .filter(p -> !p.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    public SalaryPaymentResponse update(
            Long id,
            SalaryPaymentRequest request
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        SalaryPayment payment =
                salaryPaymentRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(p -> !p.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Salary payment not found"
                                )
                        );

        Staff staff =
                staffRepository
                        .findByIdAndTenantId(
                                request.getStaffId(),
                                tenantId
                        )
                        .filter(s -> !s.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Staff not found"
                                )
                        );

        boolean duplicate =
                salaryPaymentRepository
                        .existsByStaffIdAndMonthAndYearAndTenantId(
                                staff.getId(),
                                request.getMonth(),
                                request.getYear(),
                                tenantId
                        );

        if (duplicate
                && !(
                payment.getStaff().getId()
                        .equals(staff.getId())
                        && payment.getMonth()
                        .equals(request.getMonth())
                        && payment.getYear()
                        .equals(request.getYear())
        )) {

            throw new DuplicateResourceException(
                    "Salary payment already exists for this staff member and month"
            );
        }

        if (request.getStatus()
                == com.braincampus.management.entity.SalaryPaymentStatus.PAID
                && request.getPaymentDate() == null) {

            throw new IllegalArgumentException(
                    "Payment date is required when salary is marked as PAID"
            );
        }

        payment.setStaff(staff);
        payment.setMonth(request.getMonth());
        payment.setYear(request.getYear());
        payment.setAmount(request.getAmount());
        payment.setPaymentDate(request.getPaymentDate());
        payment.setStatus(request.getStatus());
        payment.setRemarks(request.getRemarks());

        return mapToResponse(
                salaryPaymentRepository.save(payment)
        );
    }

    public void delete(Long id) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        SalaryPayment payment =
                salaryPaymentRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(p -> !p.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Salary payment not found"
                                )
                        );

        payment.setDeleted(true);

        salaryPaymentRepository.save(payment);
    }

    private SalaryPaymentResponse mapToResponse(
            SalaryPayment payment
    ) {

        Staff staff = payment.getStaff();

        return SalaryPaymentResponse.builder()
                .id(payment.getId())

                .staffId(staff.getId())
                .employeeCode(staff.getEmployeeCode())
                .staffName(
                        staff.getFirstName()
                                + " "
                                + (
                                staff.getLastName() != null
                                        ? staff.getLastName()
                                        : ""
                        )
                )

                .month(payment.getMonth())
                .year(payment.getYear())

                .amount(payment.getAmount())

                .paymentDate(payment.getPaymentDate())

                .status(payment.getStatus())

                .remarks(payment.getRemarks())

                .build();
    }
}