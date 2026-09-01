package com.braincampus.academicYear.service;

import com.braincampus.academicYear.dto.AcademicYearRequest;
import com.braincampus.academicYear.dto.AcademicYearResponse;
import com.braincampus.academicYear.entity.AcademicYear;
import com.braincampus.academicYear.repository.AcademicYearRepository;
import com.braincampus.exception.DuplicateResourceException;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AcademicYearService {

    private final AcademicYearRepository academicYearRepository;

    public AcademicYearResponse create(
            AcademicYearRequest request
    ) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        validateDates(request);

        if (academicYearRepository.existsByNameAndTenantId(
                request.getName(),
                tenantId
        )) {
            throw new DuplicateResourceException(
                    "Academic year already exists"
            );
        }

        AcademicYear academicYear = AcademicYear.builder()
                .name(request.getName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .active(false)
                .tenant(
                        SecurityUtils.getCurrentUser()
                                .getUser()
                                .getTenant()
                )
                .build();

        academicYear =
                academicYearRepository.save(academicYear);

        return mapToResponse(academicYear);
    }

    @Transactional(readOnly = true)
    public List<AcademicYearResponse> getAll() {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        return academicYearRepository
                .findAllByTenantId(tenantId)
                .stream()
                .filter(year -> !year.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AcademicYearResponse getById(Long id) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        AcademicYear academicYear =
                academicYearRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(year -> !year.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Academic year not found"
                                )
                        );

        return mapToResponse(academicYear);
    }

    @Transactional(readOnly = true)
    public AcademicYearResponse getActive() {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        AcademicYear academicYear =
                academicYearRepository
                        .findByActiveTrueAndTenantId(
                                tenantId
                        )
                        .filter(year -> !year.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Active academic year not found"
                                )
                        );

        return mapToResponse(academicYear);
    }

    public AcademicYearResponse update(
            Long id,
            AcademicYearRequest request
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        AcademicYear academicYear =
                academicYearRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(year -> !year.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Academic year not found"
                                )
                        );

        validateDates(request);

        AcademicYear existing =
                academicYearRepository
                        .findByNameAndTenantId(
                                request.getName(),
                                tenantId
                        )
                        .orElse(null);

        if (existing != null
                && !existing.getId()
                        .equals(academicYear.getId())) {

            throw new DuplicateResourceException(
                    "Academic year already exists"
            );
        }

        academicYear.setName(request.getName());
        academicYear.setStartDate(request.getStartDate());
        academicYear.setEndDate(request.getEndDate());

        return mapToResponse(
                academicYearRepository.save(academicYear)
        );
    }

    public AcademicYearResponse activate(Long id) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        AcademicYear academicYear =
                academicYearRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(year -> !year.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Academic year not found"
                                )
                        );

        // Deactivate the currently active year
        academicYearRepository
                .findByActiveTrueAndTenantId(tenantId)
                .ifPresent(currentYear -> {

                    if (!currentYear.getId()
                            .equals(academicYear.getId())) {

                        currentYear.setActive(false);
                        academicYearRepository.save(currentYear);
                    }
                });

        academicYear.setActive(true);

        return mapToResponse(
                academicYearRepository.save(academicYear)
        );
    }

    public void delete(Long id) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        AcademicYear academicYear =
                academicYearRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(year -> !year.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Academic year not found"
                                )
                        );

        if (academicYear.getActive()) {
            throw new IllegalStateException(
                    "Active academic year cannot be deleted"
            );
        }

        academicYear.setDeleted(true);
        academicYear.setActive(false);

        academicYearRepository.save(academicYear);
    }

    private void validateDates(
            AcademicYearRequest request
    ) {

        if (!request.getStartDate()
                .isBefore(request.getEndDate())) {

            throw new IllegalArgumentException(
                    "Start date must be before end date"
            );
        }
    }

    private AcademicYearResponse mapToResponse(
            AcademicYear academicYear
    ) {

        return AcademicYearResponse.builder()
                .id(academicYear.getId())
                .name(academicYear.getName())
                .startDate(academicYear.getStartDate())
                .endDate(academicYear.getEndDate())
                .active(academicYear.getActive())
                .build();
    }
}