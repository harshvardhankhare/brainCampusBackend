package com.braincampus.management.staff.service;
import com.braincampus.auth.entity.Role;
import com.braincampus.auth.entity.Tenant;
import com.braincampus.auth.entity.User;
import com.braincampus.auth.repository.RoleRepository;
import com.braincampus.auth.repository.UserRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public StaffResponse create(StaffRequest request) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        // Check employee code
        if (staffRepository.existsByEmployeeCodeAndTenantId(request.getEmployeeCode(), tenantId)) {
            throw new DuplicateResourceException(
                    "Employee code already exists"
            );
        }

        // Check email for an existing user in this school
        if (userRepository.existsByEmailAndTenant_Id(request.getEmail(), tenantId)) {
            throw new DuplicateResourceException(
                    "Email already exists"
            );
        }

        // Find the fixed role
        Role role = roleRepository.findByTenant_IdAndName(tenantId, request.getRole())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role not found"
                        )
                );
        Tenant tenant =
                SecurityUtils.getCurrentUser()
                        .getUser()
                        .getTenant();

        // Create login user
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .accountLocked(false)
                .accountExpired(false)
                .credentialsExpired(false)
                .tenant(tenant)
                .role(role)
                .build();

        user = userRepository.save(user);

        // Create staff
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
                .tenant(tenant)
                .user(user)
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

    public StaffResponse update(Long id, StaffRequest request) {

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
        User user = staff.getUser();
        if (user != null) {
            user.setEnabled(false);
        }
        staff.setDeleted(true);
        staff.setActive(false);

        staffRepository.save(staff);
    }
    public StaffResponse updateStatus(Long id, boolean active) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        Staff staff = staffRepository
                .findByIdAndTenantId(id, tenantId)
                .filter(s -> !s.getDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Staff not found"
                        )
                );

        staff.setActive(active);

        if (staff.getUser() != null) {
            staff.getUser().setEnabled(active);
        }

        staff = staffRepository.save(staff);

        return mapToResponse(staff);
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