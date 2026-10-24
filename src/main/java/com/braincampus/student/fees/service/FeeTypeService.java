package com.braincampus.student.fees.service;
import com.braincampus.exception.DuplicateResourceException;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.security.SecurityUtils;
import com.braincampus.student.fees.dto.FeeTypeRequest;
import com.braincampus.student.fees.dto.FeeTypeResponse;
import com.braincampus.student.fees.dto.FeeTypeUpdateRequest;
import com.braincampus.student.fees.entity.FeeType;
import com.braincampus.student.fees.repository.FeeTypeRepository;
import com.braincampus.student.fees.repository.StudentFeeRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FeeTypeService {

    private final FeeTypeRepository feeTypeRepository;
    private final StudentFeeRepository studentFeeRepository;

    @Transactional(readOnly = true)
    public List<FeeTypeResponse> list(boolean includeInactive) {
        Long tenantId = SecurityUtils.getCurrentTenantId();

        List<FeeType> types = includeInactive
                ? feeTypeRepository.findAllByTenantIdOrderBySortOrderAscNameAsc(tenantId)
                : feeTypeRepository.findAllByTenantIdAndIsActiveTrueOrderBySortOrderAscNameAsc(tenantId);

        return types.stream().map(this::toResponse).toList();
    }

    public FeeTypeResponse create(FeeTypeRequest request) {
        Long tenantId = SecurityUtils.getCurrentTenantId();

        String name = request.getName().trim();

        if (feeTypeRepository.existsByTenantIdAndNameIgnoreCase(tenantId, name)) {
            throw new DuplicateResourceException("A fee type with this name already exists");
        }

        FeeType ft = FeeType.builder()
                .tenant(SecurityUtils.getCurrentUser().getUser().getTenant())
                .name(name)
                .code(request.getCode())
                .isRecurring(Boolean.TRUE.equals(request.getIsRecurring()))
                .isActive(true)
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .build();

        return toResponse(feeTypeRepository.save(ft));
    }

    public FeeTypeResponse update(Long id, FeeTypeUpdateRequest request) {
        Long tenantId = SecurityUtils.getCurrentTenantId();

        FeeType ft = feeTypeRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Fee type not found"));

        if (request.getName() != null) {
            String newName = request.getName().trim();
            if (!newName.equalsIgnoreCase(ft.getName())
                    && feeTypeRepository.existsByTenantIdAndNameIgnoreCase(tenantId, newName)) {
                throw new DuplicateResourceException("A fee type with this name already exists");
            }
            ft.setName(newName);
        }

        if (request.getCode() != null)       ft.setCode(request.getCode());
        if (request.getIsRecurring() != null) ft.setIsRecurring(request.getIsRecurring());
        if (request.getIsActive()    != null) ft.setIsActive(request.getIsActive());
        if (request.getSortOrder()   != null) ft.setSortOrder(request.getSortOrder());

        return toResponse(feeTypeRepository.save(ft));
    }

    public void delete(Long id) {
        Long tenantId = SecurityUtils.getCurrentTenantId();

        FeeType ft = feeTypeRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Fee type not found"));

        long inUse = studentFeeRepository.countByTenantIdAndFeeTypeId(tenantId, id);
        if (inUse > 0) {
            throw new DuplicateResourceException(
                "This fee type is used by " + inUse
                + " fee record(s). Deactivate it instead of deleting."
            );
        }

        feeTypeRepository.delete(ft);
    }

    private FeeTypeResponse toResponse(FeeType ft) {
        return FeeTypeResponse.builder()
                .id(ft.getId())
                .name(ft.getName())
                .code(ft.getCode())
                .isRecurring(ft.getIsRecurring())
                .isActive(ft.getIsActive())
                .sortOrder(ft.getSortOrder())
                .createdAt(ft.getCreatedAt())
                .build();
    }
}