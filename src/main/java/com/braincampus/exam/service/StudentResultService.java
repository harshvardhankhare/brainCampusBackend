package com.braincampus.exam.service;
import com.braincampus.exam.dto.StudentResultRequest;
import com.braincampus.exam.dto.StudentResultResponse;
import com.braincampus.exam.entity.ExamSubject;
import com.braincampus.exam.entity.StudentResult;
import com.braincampus.exam.repository.ExamSubjectRepository;
import com.braincampus.exam.repository.StudentResultRepository;
import com.braincampus.exception.DuplicateResourceException;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.security.SecurityUtils;
import com.braincampus.student.entity.Student;
import com.braincampus.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentResultService {

    private final StudentResultRepository studentResultRepository;
    private final StudentRepository studentRepository;
    private final ExamSubjectRepository examSubjectRepository;

    public StudentResultResponse create(
            StudentResultRequest request
    ) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        Student student =
                studentRepository
                        .findByIdAndTenantId(
                                request.getStudentId(),
                                tenantId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student not found"
                                )
                        );

        ExamSubject examSubject =
                examSubjectRepository
                        .findByIdAndTenantId(
                                request.getExamSubjectId(),
                                tenantId
                        )
                        .filter(es -> !es.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Exam subject not found"
                                )
                        );

        // Student must belong to the exam's class
        if (!student.getSchoolClass().getId()
                .equals(
                        examSubject.getExam()
                                .getSchoolClass()
                                .getId()
                )) {

            throw new IllegalArgumentException(
                    "Student does not belong to the exam class"
            );
        }

        // Marks cannot exceed maximum marks
        if (request.getMarksObtained()
                .compareTo(
                        BigDecimal.valueOf(
                                examSubject.getMaxMarks()
                        )
                ) > 0) {

            throw new IllegalArgumentException(
                    "Marks obtained cannot exceed maximum marks"
            );
        }

        // Prevent duplicate result
        if (studentResultRepository
                .existsByStudentIdAndExamSubjectIdAndTenantId(
                        student.getId(),
                        examSubject.getId(),
                        tenantId
                )) {

            throw new DuplicateResourceException(
                    "Result already exists for this student and subject"
            );
        }

        StudentResult result =
                StudentResult.builder()
                        .student(student)
                        .examSubject(examSubject)
                        .marksObtained(
                                request.getMarksObtained()
                        )
                        .remarks(
                                request.getRemarks()
                        )
                        .active(true)
                        .tenant(
                                SecurityUtils
                                        .getCurrentUser()
                                        .getUser()
                                        .getTenant()
                        )
                        .build();

        result =
                studentResultRepository.save(result);

        return mapToResponse(result);
    }

    @Transactional(readOnly = true)
    public List<StudentResultResponse> getAll() {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        return studentResultRepository
                .findAllByTenantId(tenantId)
                .stream()
                .filter(result -> !result.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentResultResponse> getByStudent(
            Long studentId
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        studentRepository
                .findByIdAndTenantId(
                        studentId,
                        tenantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found"
                        )
                );

        return studentResultRepository
                .findAllByStudentIdAndTenantId(
                        studentId,
                        tenantId
                )
                .stream()
                .filter(result -> !result.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentResultResponse> getByExamSubject(
            Long examSubjectId
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        examSubjectRepository
                .findByIdAndTenantId(
                        examSubjectId,
                        tenantId
                )
                .filter(es -> !es.getDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Exam subject not found"
                        )
                );

        return studentResultRepository
                .findAllByExamSubjectIdAndTenantId(
                        examSubjectId,
                        tenantId
                )
                .stream()
                .filter(result -> !result.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentResultResponse getById(Long id) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        StudentResult result =
                studentResultRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(r -> !r.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Result not found"
                                )
                        );

        return mapToResponse(result);
    }

    public StudentResultResponse update(
            Long id,
            StudentResultRequest request
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        StudentResult result =
                studentResultRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(r -> !r.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Result not found"
                                )
                        );

        Student student =
                studentRepository
                        .findByIdAndTenantId(
                                request.getStudentId(),
                                tenantId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student not found"
                                )
                        );

        ExamSubject examSubject =
                examSubjectRepository
                        .findByIdAndTenantId(
                                request.getExamSubjectId(),
                                tenantId
                        )
                        .filter(es -> !es.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Exam subject not found"
                                )
                        );

        if (!student.getSchoolClass().getId()
                .equals(
                        examSubject.getExam()
                                .getSchoolClass()
                                .getId()
                )) {

            throw new IllegalArgumentException(
                    "Student does not belong to the exam class"
            );
        }

        if (request.getMarksObtained()
                .compareTo(
                        BigDecimal.valueOf(
                                examSubject.getMaxMarks()
                        )
                ) > 0) {

            throw new IllegalArgumentException(
                    "Marks obtained cannot exceed maximum marks"
            );
        }

        boolean duplicate =
                studentResultRepository
                        .existsByStudentIdAndExamSubjectIdAndTenantId(
                                student.getId(),
                                examSubject.getId(),
                                tenantId
                        );

        if (duplicate &&
                !(
                        result.getStudent().getId()
                                .equals(student.getId())
                        && result.getExamSubject().getId()
                                .equals(examSubject.getId())
                )) {

            throw new DuplicateResourceException(
                    "Result already exists for this student and subject"
            );
        }

        result.setStudent(student);
        result.setExamSubject(examSubject);
        result.setMarksObtained(
                request.getMarksObtained()
        );
        result.setRemarks(
                request.getRemarks()
        );

        return mapToResponse(
                studentResultRepository.save(result)
        );
    }

    public void delete(Long id) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        StudentResult result =
                studentResultRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(r -> !r.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Result not found"
                                )
                        );

        result.setDeleted(true);
        result.setActive(false);

        studentResultRepository.save(result);
    }

    private StudentResultResponse mapToResponse(
            StudentResult result
    ) {

        Student student = result.getStudent();
        ExamSubject examSubject = result.getExamSubject();

        return StudentResultResponse.builder()
                .id(result.getId())

                .studentId(student.getId())
                .studentName(
                        student.getFirstName()
                                + " "
                                + student.getLastName()
                )
                .roleNumber(student.getRoleNumber())

                .examSubjectId(examSubject.getId())

                .examId(
                        examSubject.getExam().getId()
                )
                .examName(
                        examSubject.getExam().getName()
                )

                .classId(
                        examSubject.getExam()
                                .getSchoolClass()
                                .getId()
                )
                .className(
                        examSubject.getExam()
                                .getSchoolClass()
                                .getName()
                )
                .section(
                        examSubject.getExam()
                                .getSchoolClass()
                                .getSection()
                )

                .subjectId(
                        examSubject.getClassSubject()
                                .getSubject()
                                .getId()
                )
                .subjectName(
                        examSubject.getClassSubject()
                                .getSubject()
                                .getName()
                )
                .subjectCode(
                        examSubject.getClassSubject()
                                .getSubject()
                                .getCode()
                )

                .marksObtained(
                        result.getMarksObtained()
                )
                .maxMarks(
                        examSubject.getMaxMarks()
                )
                .passingMarks(
                        examSubject.getPassingMarks()
                )

                .passed(
                        result.getMarksObtained()
                                .compareTo(
                                        BigDecimal.valueOf(
                                                examSubject
                                                        .getPassingMarks()
                                        )
                                ) >= 0
                )

                .remarks(result.getRemarks())
                .active(result.getActive())
                .build();
    }
}