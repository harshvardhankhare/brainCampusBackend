package com.braincampus.management.staff.service;
import com.braincampus.exception.DuplicateResourceException;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.security.SecurityUtils;
import com.braincampus.management.staff.dto.StaffRequest;
import com.braincampus.management.staff.dto.StaffResponse;
import com.braincampus.management.staff.entity.Staff;
import com.braincampus.management.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffService {

    private final StaffRepository staffRepository;

    public StaffResponse create(StaffRequest request) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        if (staffRepository.existsByEmployeeCodeAndTenantId(
                request.getEmployeeCode(),
                tenantId
        )) {
            throw new DuplicateResourceException(
                    "Employee code already exists"
            );
        }

        Staff staff = Staff.builder()
                .employeeCode(request.getEmployeeCode())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .joiningDate(request.getJoiningDate())
                .type(request.getType())
                .designation(request.getDesignation())
                .address(request.getAddress())
                .active(true)
                .tenant(
                        SecurityUtils.getCurrentUser()
                                .getUser()
                                .getTenant()
                )
                .build();

        staff = staffRepository.save(staff);

        return mapToResponse(staff);
    }

    @Transactional(readOnly = true)
    public List<StaffResponse> getAll() {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        return staffRepository
                .findAllByTenantId(tenantId)
                .stream()
                .filter(staff -> !staff.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StaffResponse> getByType(
            com.braincampus.management.staff.entity.StaffType type
    ) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        return staffRepository
                .findAllByTenantIdAndType(tenantId, type)
                .stream()
                .filter(staff -> !staff.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StaffResponse getById(Long id) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        Staff staff = staffRepository
                .findByIdAndTenantId(id, tenantId)
                .filter(s -> !s.getDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Staff not found"
                        )
                );

        return mapToResponse(staff);
    }

    public StaffResponse update(
            Long id,
            StaffRequest request
    ) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        Staff staff = staffRepository
                .findByIdAndTenantId(id, tenantId)
                .filter(s -> !s.getDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Staff not found"
                        )
                );

        if (!staff.getEmployeeCode()
                .equals(request.getEmployeeCode())
                && staffRepository.existsByEmployeeCodeAndTenantId(
                        request.getEmployeeCode(),
                        tenantId
                )) {

            throw new DuplicateResourceException(
                    "Employee code already exists"
            );
        }

        staff.setEmployeeCode(request.getEmployeeCode());
        staff.setFirstName(request.getFirstName());
        staff.setLastName(request.getLastName());
        staff.setEmail(request.getEmail());
        staff.setPhone(request.getPhone());
        staff.setDateOfBirth(request.getDateOfBirth());
        staff.setJoiningDate(request.getJoiningDate());
        staff.setType(request.getType());
        staff.setDesignation(request.getDesignation());
        staff.setAddress(request.getAddress());

        return mapToResponse(
                staffRepository.save(staff)
        );
    }

    public void delete(Long id) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        Staff staff = staffRepository
                .findByIdAndTenantId(id, tenantId)
                .filter(s -> !s.getDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Staff not found"
                        )
                );

        staff.setDeleted(true);
        staff.setActive(false);

        staffRepository.save(staff);
    }

    private StaffResponse mapToResponse(Staff staff) {

        return StaffResponse.builder()
                .id(staff.getId())
                .employeeCode(staff.getEmployeeCode())
                .firstName(staff.getFirstName())
                .lastName(staff.getLastName())
                .email(staff.getEmail())
                .phone(staff.getPhone())
                .dateOfBirth(staff.getDateOfBirth())
                .joiningDate(staff.getJoiningDate())
                .type(staff.getType())
                .designation(staff.getDesignation())
                .address(staff.getAddress())
                .active(staff.getActive())
                .build();
    }
}