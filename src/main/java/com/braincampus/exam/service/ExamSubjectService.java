package com.braincampus.exam.service;
import com.braincampus.classSubject.entity.ClassSubject;
import com.braincampus.classSubject.repository.ClassSubjectRepository;
import com.braincampus.exam.dto.ExamSubjectRequest;
import com.braincampus.exam.dto.ExamSubjectResponse;
import com.braincampus.exam.entity.Exam;
import com.braincampus.exam.entity.ExamSubject;
import com.braincampus.exam.repository.ExamRepository;
import com.braincampus.exam.repository.ExamSubjectRepository;
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
public class ExamSubjectService {

    private final ExamSubjectRepository examSubjectRepository;
    private final ExamRepository examRepository;
    private final ClassSubjectRepository classSubjectRepository;

    public ExamSubjectResponse create(
            ExamSubjectRequest request
    ) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        Exam exam = examRepository
                .findByIdAndTenantId(
                        request.getExamId(),
                        tenantId
                )
                .filter(e -> !e.getDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Exam not found"
                        )
                );

        ClassSubject classSubject = classSubjectRepository
                .findByIdAndTenantId(
                        request.getClassSubjectId(),
                        tenantId
                )
                .filter(cs -> !cs.getDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Class subject not found"
                        )
                );

        // Make sure the ClassSubject belongs to the same class
        if (!classSubject.getSchoolClass().getId()
                .equals(exam.getSchoolClass().getId())) {

            throw new IllegalArgumentException(
                    "Class subject does not belong to the exam class"
            );
        }

        // Make sure academic years match
        if (!classSubject.getAcademicYear()
                .equals(exam.getAcademicYear())) {

            throw new IllegalArgumentException(
                    "Academic year does not match the exam"
            );
        }

        if (request.getPassingMarks() > request.getMaxMarks()) {

            throw new IllegalArgumentException(
                    "Passing marks cannot be greater than maximum marks"
            );
        }

        if (examSubjectRepository
                .existsByExamIdAndClassSubjectIdAndTenantId(
                        exam.getId(),
                        classSubject.getId(),
                        tenantId
                )) {

            throw new DuplicateResourceException(
                    "Subject is already added to this exam"
            );
        }

        ExamSubject examSubject = ExamSubject.builder()
                .exam(exam)
                .classSubject(classSubject)
                .examDate(request.getExamDate())
                .maxMarks(request.getMaxMarks())
                .passingMarks(request.getPassingMarks())
                .remarks(request.getRemarks())
                .active(true)
                .tenant(
                        SecurityUtils.getCurrentUser()
                                .getUser()
                                .getTenant()
                )
                .build();

        examSubject =
                examSubjectRepository.save(examSubject);

        return mapToResponse(examSubject);
    }

    @Transactional(readOnly = true)
    public List<ExamSubjectResponse> getAll() {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        return examSubjectRepository
                .findAllByTenantId(tenantId)
                .stream()
                .filter(es -> !es.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExamSubjectResponse> getByExam(
            Long examId
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        examRepository
                .findByIdAndTenantId(
                        examId,
                        tenantId
                )
                .filter(e -> !e.getDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Exam not found"
                        )
                );

        return examSubjectRepository
                .findAllByExamIdAndTenantId(
                        examId,
                        tenantId
                )
                .stream()
                .filter(es -> !es.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExamSubjectResponse getById(
            Long id
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        ExamSubject examSubject =
                examSubjectRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(es -> !es.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Exam subject not found"
                                )
                        );

        return mapToResponse(examSubject);
    }

    public ExamSubjectResponse update(
            Long id,
            ExamSubjectRequest request
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        ExamSubject examSubject =
                examSubjectRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(es -> !es.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Exam subject not found"
                                )
                        );

        Exam exam =
                examRepository
                        .findByIdAndTenantId(
                                request.getExamId(),
                                tenantId
                        )
                        .filter(e -> !e.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Exam not found"
                                )
                        );

        ClassSubject classSubject =
                classSubjectRepository
                        .findByIdAndTenantId(
                                request.getClassSubjectId(),
                                tenantId
                        )
                        .filter(cs -> !cs.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Class subject not found"
                                )
                        );

        if (!classSubject.getSchoolClass().getId()
                .equals(exam.getSchoolClass().getId())) {

            throw new IllegalArgumentException(
                    "Class subject does not belong to the exam class"
            );
        }

        if (!classSubject.getAcademicYear()
                .equals(exam.getAcademicYear())) {

            throw new IllegalArgumentException(
                    "Academic year does not match the exam"
            );
        }

        if (request.getPassingMarks() > request.getMaxMarks()) {

            throw new IllegalArgumentException(
                    "Passing marks cannot be greater than maximum marks"
            );
        }

        boolean duplicate =
                examSubjectRepository
                        .existsByExamIdAndClassSubjectIdAndTenantId(
                                exam.getId(),
                                classSubject.getId(),
                                tenantId
                        );

        if (duplicate &&
                !examSubject.getId().equals(id)) {

            throw new DuplicateResourceException(
                    "Subject is already added to this exam"
            );
        }

        examSubject.setExam(exam);
        examSubject.setClassSubject(classSubject);
        examSubject.setExamDate(request.getExamDate());
        examSubject.setMaxMarks(request.getMaxMarks());
        examSubject.setPassingMarks(request.getPassingMarks());
        examSubject.setRemarks(request.getRemarks());

        return mapToResponse(
                examSubjectRepository.save(examSubject)
        );
    }

    public void delete(Long id) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        ExamSubject examSubject =
                examSubjectRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(es -> !es.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Exam subject not found"
                                )
                        );

        examSubject.setDeleted(true);
        examSubject.setActive(false);

        examSubjectRepository.save(examSubject);
    }

    private ExamSubjectResponse mapToResponse(
            ExamSubject examSubject
    ) {

        Exam exam = examSubject.getExam();
        ClassSubject classSubject =
                examSubject.getClassSubject();

        return ExamSubjectResponse.builder()
                .id(examSubject.getId())

                .examId(exam.getId())
                .examName(exam.getName())

                .classSubjectId(classSubject.getId())

                .classId(
                        classSubject.getSchoolClass().getId()
                )
                .className(
                        classSubject.getSchoolClass().getName()
                )
                .section(
                        classSubject.getSchoolClass().getSection()
                )

                .subjectId(
                        classSubject.getSubject().getId()
                )
                .subjectName(
                        classSubject.getSubject().getName()
                )
                .subjectCode(
                        classSubject.getSubject().getCode()
                )

                .teacherId(
                        classSubject.getTeacher() != null
                                ? classSubject.getTeacher().getId()
                                : null
                )

                .teacherName(
                        classSubject.getTeacher() != null
                                ? classSubject.getTeacher()
                                        .getFirstName()
                                        + " "
                                        + classSubject.getTeacher()
                                        .getLastName()
                                : null
                )

                .academicYear(
                        classSubject.getAcademicYear()
                )

                .examDate(examSubject.getExamDate())
                .maxMarks(examSubject.getMaxMarks())
                .passingMarks(examSubject.getPassingMarks())
                .remarks(examSubject.getRemarks())
                .active(examSubject.getActive())
                .build();
    }
}