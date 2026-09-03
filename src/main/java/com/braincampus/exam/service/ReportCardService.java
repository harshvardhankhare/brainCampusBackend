package com.braincampus.exam.service;
import com.braincampus.exam.dto.ReportCardResponse;
import com.braincampus.exam.dto.ReportCardSubjectResponse;
import com.braincampus.exam.entity.Exam;
import com.braincampus.exam.entity.StudentResult;
import com.braincampus.exam.repository.ExamRepository;
import com.braincampus.exam.repository.StudentResultRepository;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.security.SecurityUtils;
import com.braincampus.student.entity.Student;
import com.braincampus.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportCardService {

    private final StudentRepository studentRepository;
    private final ExamRepository examRepository;
    private final StudentResultRepository studentResultRepository;

    public ReportCardResponse getReportCard(
            Long studentId,
            Long examId
    ) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        Student student = studentRepository.findByIdAndTenantId(studentId, tenantId).orElseThrow(() ->
                        new ResourceNotFoundException("Student not found")
                        );

        Exam exam =
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

        // Student must belong to the exam's class
        if (!student.getSchoolClass().getId()
                .equals(exam.getSchoolClass().getId())) {

            throw new IllegalArgumentException(
                    "Student does not belong to the exam class"
            );
        }

        List<StudentResult> results =
                studentResultRepository
                        .findAllByStudentIdAndTenantId(
                                studentId,
                                tenantId
                        )
                        .stream()
                        .filter(result -> !result.getDeleted())
                        .filter(result ->
                                result.getExamSubject()
                                        .getExam()
                                        .getId()
                                        .equals(examId)
                        )
                        .toList();

        if (results.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No results found for this student and exam"
            );
        }

        List<ReportCardSubjectResponse> subjects =
                results.stream()
                        .map(this::mapSubject)
                        .toList();

        BigDecimal totalMarks =
                results.stream()
                        .map(StudentResult::getMarksObtained)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        int maximumMarks =
                results.stream()
                        .mapToInt(result ->
                                result.getExamSubject()
                                        .getMaxMarks()
                        )
                        .sum();

        BigDecimal percentage =
                totalMarks
                        .divide(
                                BigDecimal.valueOf(maximumMarks),
                                2,
                                RoundingMode.HALF_UP
                        )
                        .multiply(BigDecimal.valueOf(100));

        boolean passed =
                results.stream()
                        .allMatch(result ->
                                result.getMarksObtained()
                                        .compareTo(
                                                BigDecimal.valueOf(
                                                        result.getExamSubject()
                                                                .getPassingMarks()
                                                )
                                        ) >= 0
                        );

        return ReportCardResponse.builder()
                .studentId(student.getId())
                .studentName(
                        student.getFirstName()
                                + " "
                                + student.getLastName()
                )
                .roleNumber(student.getRoleNumber())

                .classId(
                        student.getSchoolClass().getId()
                )
                .className(
                        student.getSchoolClass().getName()
                )
                .section(
                        student.getSchoolClass().getSection()
                )

                .examId(exam.getId())
                .examName(exam.getName())
                .academicYear(exam.getAcademicYear())

                .subjects(subjects)

                .totalMarks(totalMarks)
                .maximumMarks(maximumMarks)
                .percentage(percentage)

                .grade(calculateGrade(percentage))

                .result(passed ? "PASS" : "FAIL")

                .build();
    }

    private ReportCardSubjectResponse mapSubject(
            StudentResult result
    ) {

        var examSubject = result.getExamSubject();

        var subject =
                examSubject
                        .getClassSubject()
                        .getSubject();

        boolean passed =
                result.getMarksObtained()
                        .compareTo(
                                BigDecimal.valueOf(
                                        examSubject
                                                .getPassingMarks()
                                )
                        ) >= 0;

        BigDecimal percentage =
                result.getMarksObtained()
                        .divide(
                                BigDecimal.valueOf(
                                        examSubject.getMaxMarks()
                                ),
                                4,
                                RoundingMode.HALF_UP
                        )
                        .multiply(BigDecimal.valueOf(100));

        return ReportCardSubjectResponse.builder()
                .subjectId(subject.getId())
                .subjectName(subject.getName())
                .subjectCode(subject.getCode())

                .maxMarks(
                        examSubject.getMaxMarks()
                )
                .passingMarks(
                        examSubject.getPassingMarks()
                )

                .marksObtained(
                        result.getMarksObtained()
                )

                .grade(
                        calculateGrade(percentage)
                )

                .passed(passed)

                .build();
    }

    private String calculateGrade(
            BigDecimal percentage
    ) {

        if (percentage.compareTo(
                BigDecimal.valueOf(90)) >= 0) {
            return "A+";
        }

        if (percentage.compareTo(
                BigDecimal.valueOf(80)) >= 0) {
            return "A";
        }

        if (percentage.compareTo(
                BigDecimal.valueOf(70)) >= 0) {
            return "B+";
        }

        if (percentage.compareTo(
                BigDecimal.valueOf(60)) >= 0) {
            return "B";
        }

        if (percentage.compareTo(
                BigDecimal.valueOf(50)) >= 0) {
            return "C";
        }

        if (percentage.compareTo(
                BigDecimal.valueOf(40)) >= 0) {
            return "D";
        }

        return "F";
    }
}