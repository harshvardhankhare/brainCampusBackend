package com.braincampus.management.service;

import com.braincampus.exception.DuplicateResourceException;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.management.dto.SalaryStructureRequest;
import com.braincampus.management.dto.SalaryStructureResponse;
import com.braincampus.management.entity.SalaryStructure;
import com.braincampus.management.repository.SalaryStructureRepository;
import com.braincampus.security.SecurityUtils;
import com.braincampus.management.staff.entity.Staff;
import com.braincampus.management.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SalaryStructureService {

    private final SalaryStructureRepository salaryStructureRepository;
    private final StaffRepository staffRepository;

    public SalaryStructureResponse create(
            SalaryStructureRequest request
    ) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        Staff staff = staffRepository
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

        validateDates(request.getEffectiveFrom(),
                request.getEffectiveTo());

        validateNoOverlap(
                staff.getId(),
                tenantId,
                request.getEffectiveFrom(),
                request.getEffectiveTo(),
                null
        );

        SalaryStructure salaryStructure =
                SalaryStructure.builder()
                        .staff(staff)
                        .basicSalary(request.getBasicSalary())
                        .allowances(
                                request.getAllowances() != null
                                        ? request.getAllowances()
                                        : BigDecimal.ZERO
                        )
                        .deductions(
                                request.getDeductions() != null
                                        ? request.getDeductions()
                                        : BigDecimal.ZERO
                        )
                        .effectiveFrom(
                                request.getEffectiveFrom()
                        )
                        .effectiveTo(
                                request.getEffectiveTo()
                        )
                        .active(true)
                        .tenant(
                                SecurityUtils
                                        .getCurrentUser()
                                        .getUser()
                                        .getTenant()
                        )
                        .build();

        salaryStructure =
                salaryStructureRepository.save(
                        salaryStructure
                );

        return mapToResponse(salaryStructure);
    }

    @Transactional(readOnly = true)
    public List<SalaryStructureResponse> getAll() {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        return salaryStructureRepository
                .findAllByTenantId(tenantId)
                .stream()
                .filter(s -> !s.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SalaryStructureResponse getById(
            Long id
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        SalaryStructure salaryStructure =
                salaryStructureRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(s -> !s.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Salary structure not found"
                                )
                        );

        return mapToResponse(salaryStructure);
    }

    @Transactional(readOnly = true)
    public List<SalaryStructureResponse> getByStaff(
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

        return salaryStructureRepository
                .findAllByStaffIdAndTenantId(
                        staffId,
                        tenantId
                )
                .stream()
                .filter(s -> !s.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SalaryStructureResponse getCurrentSalary(
            Long staffId
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        SalaryStructure salaryStructure =
                salaryStructureRepository
                        .findByStaffIdAndActiveTrueAndTenantId(
                                staffId,
                                tenantId
                        )
                        .filter(s -> !s.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Active salary structure not found"
                                )
                        );

        return mapToResponse(salaryStructure);
    }

    public SalaryStructureResponse update(
            Long id,
            SalaryStructureRequest request
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        SalaryStructure salaryStructure =
                salaryStructureRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(s -> !s.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Salary structure not found"
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

        validateDates(
                request.getEffectiveFrom(),
                request.getEffectiveTo()
        );

        validateNoOverlap(
                staff.getId(),
                tenantId,
                request.getEffectiveFrom(),
                request.getEffectiveTo(),
                salaryStructure.getId()
        );

        salaryStructure.setStaff(staff);

        salaryStructure.setBasicSalary(
                request.getBasicSalary()
        );

        salaryStructure.setAllowances(
                request.getAllowances() != null
                        ? request.getAllowances()
                        : BigDecimal.ZERO
        );

        salaryStructure.setDeductions(
                request.getDeductions() != null
                        ? request.getDeductions()
                        : BigDecimal.ZERO
        );

        salaryStructure.setEffectiveFrom(
                request.getEffectiveFrom()
        );

        salaryStructure.setEffectiveTo(
                request.getEffectiveTo()
        );

        return mapToResponse(
                salaryStructureRepository.save(
                        salaryStructure
                )
        );
    }

    public void delete(Long id) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        SalaryStructure salaryStructure =
                salaryStructureRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(s -> !s.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Salary structure not found"
                                )
                        );

        salaryStructure.setDeleted(true);
        salaryStructure.setActive(false);

        salaryStructureRepository.save(
                salaryStructure
        );
    }

    private void validateDates(
            LocalDate effectiveFrom,
            LocalDate effectiveTo
    ) {

        if (effectiveTo != null
                && !effectiveFrom.isBefore(effectiveTo)) {

            throw new IllegalArgumentException(
                    "Effective from date must be before effective to date"
            );
        }
    }

    private void validateNoOverlap(
            Long staffId,
            Long tenantId,
            LocalDate newFrom,
            LocalDate newTo,
            Long excludeId
    ) {

        List<SalaryStructure> existingStructures =
                salaryStructureRepository
                        .findAllByStaffIdAndTenantId(
                                staffId,
                                tenantId
                        );

        for (SalaryStructure existing :
                existingStructures) {

            if (existing.getDeleted()) {
                continue;
            }

            if (excludeId != null
                    && existing.getId()
                    .equals(excludeId)) {
                continue;
            }

            LocalDate existingFrom =
                    existing.getEffectiveFrom();

            LocalDate existingTo =
                    existing.getEffectiveTo();

            boolean overlaps;

            if (newTo == null && existingTo == null) {

                overlaps = true;

            } else if (newTo == null) {

                overlaps =
                        !newFrom.isAfter(existingTo);

            } else if (existingTo == null) {

                overlaps =
                        !existingFrom.isAfter(newTo);

            } else {

                overlaps =
                        !newFrom.isAfter(existingTo)
                                && !existingFrom.isAfter(newTo);
            }

            if (overlaps) {

                throw new DuplicateResourceException(
                        "Salary period overlaps with an existing salary structure"
                );
            }
        }
    }

    private SalaryStructureResponse mapToResponse(
            SalaryStructure salaryStructure
    ) {

        BigDecimal netSalary =
                salaryStructure.getBasicSalary()
                        .add(salaryStructure.getAllowances())
                        .subtract(salaryStructure.getDeductions());

        Staff staff = salaryStructure.getStaff();

        return SalaryStructureResponse.builder()
                .id(salaryStructure.getId())

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

                .basicSalary(
                        salaryStructure.getBasicSalary()
                )
                .allowances(
                        salaryStructure.getAllowances()
                )
                .deductions(
                        salaryStructure.getDeductions()
                )
                .netSalary(netSalary)

                .effectiveFrom(
                        salaryStructure.getEffectiveFrom()
                )
                .effectiveTo(
                        salaryStructure.getEffectiveTo()
                )

                .active(
                        salaryStructure.getActive()
                )

                .build();
    }
}