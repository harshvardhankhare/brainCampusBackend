package com.braincampus.exam.service;
import com.braincampus.exam.dto.ExamRequest;
import com.braincampus.exam.dto.ExamResponse;
import com.braincampus.exam.entity.Exam;
import com.braincampus.exam.repository.ExamRepository;
import com.braincampus.exception.DuplicateResourceException;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.schoolClass.entity.SchoolClass;
import com.braincampus.schoolClass.repository.SchoolClassRepository;
import com.braincampus.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExamService {

    private final ExamRepository examRepository;
    private final SchoolClassRepository schoolClassRepository;

    public ExamResponse create(ExamRequest request) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        SchoolClass schoolClass =
                schoolClassRepository
                        .findByIdAndTenantId(
                                request.getClassId(),
                                tenantId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Class not found"
                                )
                        );

        if (examRepository
                .existsBySchoolClassIdAndNameAndAcademicYearAndTenantId(
                        schoolClass.getId(),
                        request.getName(),
                        request.getAcademicYear(),
                        tenantId
                )) {

            throw new DuplicateResourceException(
                    "Exam already exists for this class and academic year"
            );
        }

        Exam exam = Exam.builder()
                .name(request.getName())
                .academicYear(request.getAcademicYear())
                .schoolClass(schoolClass)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .active(true)
                .tenant(
                        SecurityUtils.getCurrentUser()
                                .getUser()
                                .getTenant()
                )
                .build();

        exam = examRepository.save(exam);

        return mapToResponse(exam);
    }

    @Transactional(readOnly = true)
    public List<ExamResponse> getAll() {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        return examRepository
                .findAllByTenantId(tenantId)
                .stream()
                .filter(exam -> !exam.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExamResponse> getByClass(Long classId) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        schoolClassRepository
                .findByIdAndTenantId(
                        classId,
                        tenantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Class not found"
                        )
                );

        return examRepository
                .findAllBySchoolClassIdAndTenantId(
                        classId,
                        tenantId
                )
                .stream()
                .filter(exam -> !exam.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExamResponse getById(Long id) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        Exam exam =
                examRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(e -> !e.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Exam not found"
                                )
                        );

        return mapToResponse(exam);
    }

    public ExamResponse update(
            Long id,
            ExamRequest request
    ) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        Exam exam =
                examRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(e -> !e.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Exam not found"
                                )
                        );

        SchoolClass schoolClass =
                schoolClassRepository
                        .findByIdAndTenantId(
                                request.getClassId(),
                                tenantId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Class not found"
                                )
                        );

        boolean duplicate =
                examRepository
                        .existsBySchoolClassIdAndNameAndAcademicYearAndTenantId(
                                schoolClass.getId(),
                                request.getName(),
                                request.getAcademicYear(),
                                tenantId
                        );

        if (duplicate &&
                !(
                        exam.getSchoolClass().getId()
                                .equals(schoolClass.getId())
                        && exam.getName()
                                .equals(request.getName())
                        && exam.getAcademicYear()
                                .equals(request.getAcademicYear())
                )) {

            throw new DuplicateResourceException(
                    "Exam already exists for this class and academic year"
            );
        }

        exam.setName(request.getName());
        exam.setAcademicYear(request.getAcademicYear());
        exam.setSchoolClass(schoolClass);
        exam.setStartDate(request.getStartDate());
        exam.setEndDate(request.getEndDate());

        exam = examRepository.save(exam);

        return mapToResponse(exam);
    }

    public void delete(Long id) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        Exam exam =
                examRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(e -> !e.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Exam not found"
                                )
                        );

        exam.setDeleted(true);
        exam.setActive(false);

        examRepository.save(exam);
    }

    private ExamResponse mapToResponse(Exam exam) {

        SchoolClass schoolClass =
                exam.getSchoolClass();

        return ExamResponse.builder()
                .id(exam.getId())
                .name(exam.getName())
                .academicYear(exam.getAcademicYear())
                .classId(schoolClass.getId())
                .className(schoolClass.getName())
                .section(schoolClass.getSection())
                .startDate(exam.getStartDate())
                .endDate(exam.getEndDate())
                .active(exam.getActive())
                .build();
    }
}