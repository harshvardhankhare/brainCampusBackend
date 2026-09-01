package com.braincampus.teacher.service;
import com.braincampus.auth.entity.Tenant;
import com.braincampus.auth.entity.User;
import com.braincampus.auth.repository.UserRepository;
import com.braincampus.exception.DuplicateResourceException;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.security.userDetails.CustomUserDetails;
import com.braincampus.teacher.dto.TeacherRequest;
import com.braincampus.teacher.dto.TeacherResponse;
import com.braincampus.teacher.entity.Teacher;
import com.braincampus.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;

    public TeacherResponse create(TeacherRequest request) {

        Tenant tenant = getCurrentTenant();

        if (teacherRepository.existsByEmployeeCodeAndTenantId(
                request.getEmployeeCode(),
                tenant.getId()
        )) {
            throw new DuplicateResourceException(
                    "Employee code already exists"
            );
        }

        if (teacherRepository.existsByEmailAndTenantId(
                request.getEmail(),
                tenant.getId()
        )) {
            throw new DuplicateResourceException(
                    "Teacher email already exists"
            );
        }

        Teacher teacher = Teacher.builder()
                .employeeCode(request.getEmployeeCode())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .active(true)
                .tenant(tenant)
                .build();

        teacher = teacherRepository.save(teacher);

        return mapToResponse(teacher);
    }

    @Transactional(readOnly = true)
    public List<TeacherResponse> getAll() {

        Tenant tenant = getCurrentTenant();

        return teacherRepository
                .findAllByTenantId(tenant.getId())
                .stream()
                .filter(teacher -> !teacher.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TeacherResponse getById(Long id) {

        Tenant tenant = getCurrentTenant();

        Teacher teacher = teacherRepository
                .findByIdAndTenantId(
                        id,
                        tenant.getId()
                )
                .filter(t -> !t.getDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Teacher not found"
                        )
                );

        return mapToResponse(teacher);
    }

    public TeacherResponse update(
            Long id,
            TeacherRequest request
    ) {

        Tenant tenant = getCurrentTenant();

        Teacher teacher = teacherRepository
                .findByIdAndTenantId(
                        id,
                        tenant.getId()
                )
                .filter(t -> !t.getDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Teacher not found"
                        )
                );

        if (!teacher.getEmployeeCode()
                .equals(request.getEmployeeCode())
                && teacherRepository
                .existsByEmployeeCodeAndTenantId(
                        request.getEmployeeCode(),
                        tenant.getId()
                )) {

            throw new DuplicateResourceException(
                    "Employee code already exists"
            );
        }

        if (!teacher.getEmail()
                .equalsIgnoreCase(request.getEmail())
                && teacherRepository
                .existsByEmailAndTenantId(
                        request.getEmail(),
                        tenant.getId()
                )) {

            throw new DuplicateResourceException(
                    "Teacher email already exists"
            );
        }

        teacher.setEmployeeCode(
                request.getEmployeeCode()
        );

        teacher.setFirstName(
                request.getFirstName()
        );

        teacher.setLastName(
                request.getLastName()
        );

        teacher.setEmail(
                request.getEmail()
        );

        teacher.setPhone(
                request.getPhone()
        );

        return mapToResponse(
                teacherRepository.save(teacher)
        );
    }

    public void delete(Long id) {

        Tenant tenant = getCurrentTenant();

        Teacher teacher = teacherRepository
                .findByIdAndTenantId(
                        id,
                        tenant.getId()
                )
                .filter(t -> !t.getDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Teacher not found"
                        )
                );

        teacher.setDeleted(true);
        teacher.setActive(false);

        teacherRepository.save(teacher);
    }

    private Tenant getCurrentTenant() {

        CustomUserDetails userDetails =
                (CustomUserDetails)
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();

        User user = userRepository
                .findById(
                        userDetails.getUserId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        return user.getTenant();
    }

    private TeacherResponse mapToResponse(
            Teacher teacher
    ) {

        return TeacherResponse.builder()
                .id(teacher.getId())
                .employeeCode(
                        teacher.getEmployeeCode()
                )
                .firstName(
                        teacher.getFirstName()
                )
                .lastName(
                        teacher.getLastName()
                )
                .email(
                        teacher.getEmail()
                )
                .phone(
                        teacher.getPhone()
                )
                .active(
                        teacher.getActive()
                )
                .build();
    }
}