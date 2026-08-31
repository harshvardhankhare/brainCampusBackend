package com.braincampus.subject.service;
import com.braincampus.auth.entity.Tenant;
import com.braincampus.auth.entity.User;
import com.braincampus.auth.repository.UserRepository;
import com.braincampus.exception.DuplicateResourceException;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.security.userDetails.CustomUserDetails;
import com.braincampus.subject.dto.SubjectRequest;
import com.braincampus.subject.dto.SubjectResponse;
import com.braincampus.subject.entity.Subject;
import com.braincampus.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    public SubjectResponse create(SubjectRequest request) {

        Tenant tenant = getCurrentTenant();

        if (subjectRepository.existsByCodeAndTenantId(
                request.getCode(),
                tenant.getId()
        )) {
            throw new DuplicateResourceException(
                    "Subject code already exists"
            );
        }

        Subject subject = Subject.builder()
                .name(request.getName())
                .code(request.getCode())
                .active(true)
                .tenant(tenant)
                .build();

        subject = subjectRepository.save(subject);

        return mapToResponse(subject);
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> getAll() {

        Tenant tenant = getCurrentTenant();

        return subjectRepository
                .findAllByTenantId(tenant.getId())
                .stream()
                .filter(subject -> !subject.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SubjectResponse getById(Long id) {

        Tenant tenant = getCurrentTenant();

        Subject subject = subjectRepository
                .findByIdAndTenantId(id, tenant.getId())
                .filter(s -> !s.getDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subject not found"
                        )
                );

        return mapToResponse(subject);
    }

    public SubjectResponse update(
            Long id,
            SubjectRequest request
    ) {

        Tenant tenant = getCurrentTenant();

        Subject subject = subjectRepository
                .findByIdAndTenantId(id, tenant.getId())
                .filter(s -> !s.getDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subject not found"
                        )
                );

        if (!subject.getCode().equals(request.getCode())
                && subjectRepository.existsByCodeAndTenantId(
                request.getCode(),
                tenant.getId()
        )) {

            throw new DuplicateResourceException(
                    "Subject code already exists"
            );
        }

        subject.setName(request.getName());
        subject.setCode(request.getCode());

        return mapToResponse(
                subjectRepository.save(subject)
        );
    }

    public void delete(Long id) {

        Tenant tenant = getCurrentTenant();

        Subject subject = subjectRepository
                .findByIdAndTenantId(id, tenant.getId())
                .filter(s -> !s.getDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subject not found"
                        )
                );

        subject.setDeleted(true);
        subject.setActive(false);

        subjectRepository.save(subject);
    }

    private Tenant getCurrentTenant() {

        CustomUserDetails userDetails =
                (CustomUserDetails)
                        SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getPrincipal();

        User user = userRepository
                .findById(userDetails.getUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );

        return user.getTenant();
    }

    private SubjectResponse mapToResponse(
            Subject subject
    ) {

        return SubjectResponse.builder()
                .id(subject.getId())
                .name(subject.getName())
                .code(subject.getCode())
                .active(subject.getActive())
                .build();
    }
}