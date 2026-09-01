package com.braincampus.academicYear.service;

import com.braincampus.academicYear.dto.StudentEnrollmentRequest;
import com.braincampus.academicYear.dto.StudentEnrollmentResponse;
import com.braincampus.academicYear.entity.AcademicYear;
import com.braincampus.academicYear.entity.StudentEnrollment;
import com.braincampus.academicYear.repository.AcademicYearRepository;
import com.braincampus.academicYear.repository.StudentEnrollmentRepository;
import com.braincampus.exception.DuplicateResourceException;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.schoolClass.entity.SchoolClass;
import com.braincampus.schoolClass.repository.SchoolClassRepository;
import com.braincampus.security.SecurityUtils;
import com.braincampus.student.entity.Student;
import com.braincampus.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentEnrollmentService {

    private final StudentEnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final AcademicYearRepository academicYearRepository;
    private final SchoolClassRepository schoolClassRepository;

    public StudentEnrollmentResponse create(
            StudentEnrollmentRequest request
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

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

        AcademicYear academicYear =
                academicYearRepository
                        .findByIdAndTenantId(
                                request.getAcademicYearId(),
                                tenantId
                        )
                        .filter(year -> !year.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Academic year not found"
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

        if (enrollmentRepository
                .existsByStudentIdAndAcademicYearIdAndTenantId(
                        student.getId(),
                        academicYear.getId(),
                        tenantId
                )) {

            throw new DuplicateResourceException(
                    "Student is already enrolled for this academic year"
            );
        }

        // Class must belong to the same academic year
        if (!schoolClass.getAcademicYear()
                .equals(academicYear.getName())) {

            throw new IllegalArgumentException(
                    "Class academic year does not match enrollment academic year"
            );
        }

        StudentEnrollment enrollment =
                StudentEnrollment.builder()
                        .student(student)
                        .academicYear(academicYear)
                        .schoolClass(schoolClass)
                        .active(true)
                        .tenant(
                                SecurityUtils
                                        .getCurrentUser()
                                        .getUser()
                                        .getTenant()
                        )
                        .build();

        enrollment =
                enrollmentRepository.save(enrollment);

        return mapToResponse(enrollment);
    }

    @Transactional(readOnly = true)
    public List<StudentEnrollmentResponse> getAll() {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        return enrollmentRepository
                .findAllByTenantId(tenantId)
                .stream()
                .filter(e -> !e.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentEnrollmentResponse> getByStudent(
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

        return enrollmentRepository
                .findAllByStudentIdAndTenantId(
                        studentId,
                        tenantId
                )
                .stream()
                .filter(e -> !e.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentEnrollmentResponse> getByAcademicYear(
            Long academicYearId
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        academicYearRepository
                .findByIdAndTenantId(
                        academicYearId,
                        tenantId
                )
                .filter(year -> !year.getDeleted())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Academic year not found"
                        )
                );

        return enrollmentRepository
                .findAllByAcademicYearIdAndTenantId(
                        academicYearId,
                        tenantId
                )
                .stream()
                .filter(e -> !e.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentEnrollmentResponse> getByClass(
            Long classId,
            Long academicYearId
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        return enrollmentRepository
                .findAllBySchoolClassIdAndAcademicYearIdAndTenantId(
                        classId,
                        academicYearId,
                        tenantId
                )
                .stream()
                .filter(e -> !e.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentEnrollmentResponse getById(
            Long id
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        StudentEnrollment enrollment =
                enrollmentRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(e -> !e.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student enrollment not found"
                                )
                        );

        return mapToResponse(enrollment);
    }

    public StudentEnrollmentResponse update(
            Long id,
            StudentEnrollmentRequest request
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        StudentEnrollment enrollment =
                enrollmentRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(e -> !e.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student enrollment not found"
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

        AcademicYear academicYear =
                academicYearRepository
                        .findByIdAndTenantId(
                                request.getAcademicYearId(),
                                tenantId
                        )
                        .filter(year -> !year.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Academic year not found"
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

        if (!schoolClass.getAcademicYear()
                .equals(academicYear.getName())) {

            throw new IllegalArgumentException(
                    "Class academic year does not match enrollment academic year"
            );
        }

        enrollment.setStudent(student);
        enrollment.setAcademicYear(academicYear);
        enrollment.setSchoolClass(schoolClass);

        return mapToResponse(
                enrollmentRepository.save(enrollment)
        );
    }

    public void delete(Long id) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        StudentEnrollment enrollment =
                enrollmentRepository
                        .findByIdAndTenantId(
                                id,
                                tenantId
                        )
                        .filter(e -> !e.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student enrollment not found"
                                )
                        );

        enrollment.setDeleted(true);
        enrollment.setActive(false);

        enrollmentRepository.save(enrollment);
    }

    private StudentEnrollmentResponse mapToResponse(
            StudentEnrollment enrollment
    ) {

        Student student = enrollment.getStudent();
        AcademicYear academicYear =
                enrollment.getAcademicYear();
        SchoolClass schoolClass =
                enrollment.getSchoolClass();

        return StudentEnrollmentResponse.builder()
                .id(enrollment.getId())

                .studentId(student.getId())
                .studentName(
                        student.getFirstName()
                                + " "
                                + student.getLastName()
                )
                .roleNumber(student.getRoleNumber())

                .academicYearId(academicYear.getId())
                .academicYear(academicYear.getName())

                .classId(schoolClass.getId())
                .className(schoolClass.getName())
                .section(schoolClass.getSection())

                .active(enrollment.getActive())

                .build();
    }
}