package com.braincampus.academicYear.service;

import com.braincampus.academicYear.dto.PromotionRequest;
import com.braincampus.academicYear.dto.PromotionResponse;
import com.braincampus.academicYear.entity.AcademicYear;
import com.braincampus.academicYear.entity.StudentEnrollment;
import com.braincampus.academicYear.repository.AcademicYearRepository;
import com.braincampus.academicYear.repository.StudentEnrollmentRepository;
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
public class PromotionService {

    private final StudentEnrollmentRepository enrollmentRepository;
    private final AcademicYearRepository academicYearRepository;
    private final SchoolClassRepository schoolClassRepository;

    public PromotionResponse promote(
            PromotionRequest request
    ) {

        Long tenantId =
                SecurityUtils.getCurrentTenantId();

        // 1. Find source academic year
        AcademicYear fromYear =
                academicYearRepository
                        .findByIdAndTenantId(
                                request.getFromAcademicYearId(),
                                tenantId
                        )
                        .filter(year -> !year.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Source academic year not found"
                                )
                        );

        // 2. Find target academic year
        AcademicYear toYear =
                academicYearRepository
                        .findByIdAndTenantId(
                                request.getToAcademicYearId(),
                                tenantId
                        )
                        .filter(year -> !year.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Target academic year not found"
                                )
                        );

        // 3. Find source class
        SchoolClass fromClass =
                schoolClassRepository
                        .findByIdAndTenantId(
                                request.getFromClassId(),
                                tenantId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Source class not found"
                                )
                        );

        // 4. Find target class
        SchoolClass toClass =
                schoolClassRepository
                        .findByIdAndTenantId(
                                request.getToClassId(),
                                tenantId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Target class not found"
                                )
                        );

        // 5. Make sure classes belong to correct academic years
        if (!fromClass.getAcademicYear()
                .equals(fromYear.getName())) {

            throw new IllegalArgumentException(
                    "Source class does not belong to source academic year"
            );
        }

        if (!toClass.getAcademicYear()
                .equals(toYear.getName())) {

            throw new IllegalArgumentException(
                    "Target class does not belong to target academic year"
            );
        }

        // 6. Get all students from source class/year
        List<StudentEnrollment> sourceEnrollments =
                enrollmentRepository
                        .findAllBySchoolClassIdAndAcademicYearIdAndTenantId(
                                fromClass.getId(),
                                fromYear.getId(),
                                tenantId
                        )
                        .stream()
                        .filter(enrollment -> !enrollment.getDeleted())
                        .toList();

        int totalStudents = sourceEnrollments.size();
        int promotedStudents = 0;

        // 7. Create target enrollment for each student
        for (StudentEnrollment sourceEnrollment :
                sourceEnrollments) {

            Long studentId =
                    sourceEnrollment
                            .getStudent()
                            .getId();

            // Already enrolled in target year?
            boolean alreadyEnrolled =
                    enrollmentRepository
                            .existsByStudentIdAndAcademicYearIdAndTenantId(
                                    studentId,
                                    toYear.getId(),
                                    tenantId
                            );

            if (alreadyEnrolled) {
                continue;
            }

            StudentEnrollment newEnrollment =
                    StudentEnrollment.builder()
                            .student(
                                    sourceEnrollment.getStudent()
                            )
                            .academicYear(toYear)
                            .schoolClass(toClass)
                            .active(true)
                            .tenant(
                                    SecurityUtils
                                            .getCurrentUser()
                                            .getUser()
                                            .getTenant()
                            )
                            .build();

            enrollmentRepository.save(newEnrollment);

            promotedStudents++;
        }

        return PromotionResponse.builder()
                .fromAcademicYearId(fromYear.getId())
                .fromAcademicYear(fromYear.getName())

                .fromClassId(fromClass.getId())
                .fromClassName(fromClass.getName())
                .fromSection(fromClass.getSection())

                .toAcademicYearId(toYear.getId())
                .toAcademicYear(toYear.getName())

                .toClassId(toClass.getId())
                .toClassName(toClass.getName())
                .toSection(toClass.getSection())

                .totalStudents(totalStudents)
                .promotedStudents(promotedStudents)

                .build();
    }
}