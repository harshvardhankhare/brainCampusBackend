package com.braincampus.schoolClass.service;
import com.braincampus.exception.DuplicateResourceException;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.auth.entity.Tenant;
import com.braincampus.auth.entity.User;
import com.braincampus.auth.repository.UserRepository;
import com.braincampus.schoolClass.dto.SchoolClassRequest;
import com.braincampus.schoolClass.dto.SchoolClassResponse;
import com.braincampus.schoolClass.entity.SchoolClass;
import com.braincampus.schoolClass.repository.SchoolClassRepository;
import com.braincampus.security.userDetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SchoolClassService {

    private final SchoolClassRepository schoolClassRepository;
    private final UserRepository userRepository;

    public SchoolClassResponse create(SchoolClassRequest request) {

        Tenant tenant = getCurrentTenant();

        if (schoolClassRepository
                .existsByNameAndSectionAndAcademicYearAndTenantId(
                        request.getName(),
                        request.getSection(),
                        request.getAcademicYear(),
                        tenant.getId()
                )) {

            throw new DuplicateResourceException(
                    "Class already exists for this academic year"
            );
        }

        SchoolClass schoolClass = SchoolClass.builder()
                .name(request.getName())
                .section(request.getSection())
                .academicYear(request.getAcademicYear())
                .active(true)
                .tenant(tenant)
                .build();

        schoolClass =
                schoolClassRepository.save(schoolClass);

        return mapToResponse(schoolClass);
    }

    @Transactional(readOnly = true)
    public List<SchoolClassResponse> getAll() {

        Tenant tenant = getCurrentTenant();

        return schoolClassRepository
                .findAllByTenantId(tenant.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SchoolClassResponse getById(Long id) {

        Tenant tenant = getCurrentTenant();

        SchoolClass schoolClass =
                schoolClassRepository
                        .findByIdAndTenantId(
                                id,
                                tenant.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Class not found"
                                )
                        );

        return mapToResponse(schoolClass);
    }

    public SchoolClassResponse update(
            Long id,
            SchoolClassRequest request
    ) {

        Tenant tenant = getCurrentTenant();

        SchoolClass schoolClass =
                schoolClassRepository
                        .findByIdAndTenantId(
                                id,
                                tenant.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Class not found"
                                )
                        );

        boolean duplicate =
                schoolClassRepository
                        .existsByNameAndSectionAndAcademicYearAndTenantId(
                                request.getName(),
                                request.getSection(),
                                request.getAcademicYear(),
                                tenant.getId()
                        );

        if (duplicate
                && !(
                schoolClass.getName()
                        .equals(request.getName())
                        && schoolClass.getSection()
                        .equals(request.getSection())
                        && schoolClass.getAcademicYear()
                        .equals(request.getAcademicYear())
        )) {

            throw new DuplicateResourceException(
                    "Class already exists for this academic year"
            );
        }

        schoolClass.setName(request.getName());
        schoolClass.setSection(request.getSection());
        schoolClass.setAcademicYear(
                request.getAcademicYear()
        );

        return mapToResponse(
                schoolClassRepository.save(schoolClass)
        );
    }

    public void delete(Long id) {

        Tenant tenant = getCurrentTenant();

        SchoolClass schoolClass =
                schoolClassRepository
                        .findByIdAndTenantId(
                                id,
                                tenant.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Class not found"
                                )
                        );

        /*
         * Soft delete.
         */
        schoolClass.setDeleted(true);
        schoolClass.setActive(false);

        schoolClassRepository.save(schoolClass);
    }

    private Tenant getCurrentTenant() {

        CustomUserDetails userDetails =
                (CustomUserDetails)
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();

        User user =
                userRepository
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

    private SchoolClassResponse mapToResponse(
            SchoolClass schoolClass
    ) {

        return SchoolClassResponse.builder()
                .id(schoolClass.getId())
                .name(schoolClass.getName())
                .section(schoolClass.getSection())
                .academicYear(
                        schoolClass.getAcademicYear()
                )
                .active(schoolClass.getActive())
                .build();
    }
}