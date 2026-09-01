package com.braincampus.classSubject.service;

import com.braincampus.auth.entity.Tenant;
import com.braincampus.auth.entity.User;
import com.braincampus.auth.repository.UserRepository;
import com.braincampus.classSubject.dto.ClassSubjectRequest;
import com.braincampus.classSubject.dto.ClassSubjectResponse;
import com.braincampus.classSubject.entity.ClassSubject;
import com.braincampus.classSubject.repository.ClassSubjectRepository;
import com.braincampus.exception.DuplicateResourceException;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.schoolClass.entity.SchoolClass;
import com.braincampus.schoolClass.repository.SchoolClassRepository;
import com.braincampus.security.userDetails.CustomUserDetails;
import com.braincampus.subject.entity.Subject;
import com.braincampus.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.braincampus.teacher.entity.Teacher;
import com.braincampus.teacher.repository.TeacherRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassSubjectService {

    private final ClassSubjectRepository classSubjectRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;

    public ClassSubjectResponse create(
            ClassSubjectRequest request
    ) {

        Tenant tenant = getCurrentTenant();

        SchoolClass schoolClass =
                schoolClassRepository
                        .findByIdAndTenantId(
                                request.getClassId(),
                                tenant.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Class not found"
                                )
                        );

        Subject subject =
                subjectRepository
                        .findByIdAndTenantId(
                                request.getSubjectId(),
                                tenant.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Subject not found"
                                )
                        );

        if (classSubjectRepository
                .existsBySchoolClassIdAndSubjectIdAndAcademicYearAndTenantId(
                        schoolClass.getId(),
                        subject.getId(),
                        request.getAcademicYear(),
                        tenant.getId()
                )) {

            throw new DuplicateResourceException(
                    "Subject is already assigned to this class for this academic year"
            );
        }
        Teacher teacher = null;

        if (request.getTeacherId() != null) {

            teacher = teacherRepository
                    .findByIdAndTenantId(
                            request.getTeacherId(),
                            tenant.getId()
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Teacher not found"
                            )
                    );
        }
        ClassSubject classSubject =
                ClassSubject.builder()
                        .schoolClass(schoolClass)
                        .subject(subject)
                        .teacher(teacher)
                        .academicYear(
                                request.getAcademicYear()
                        )
                        .weeklyPeriods(
                                request.getWeeklyPeriods()
                        )
                        .active(true)
                        .tenant(tenant)
                        .build();

        classSubject =
                classSubjectRepository.save(
                        classSubject
                );

        return mapToResponse(classSubject);
    }

    @Transactional(readOnly = true)
    public List<ClassSubjectResponse> getAll() {

        Tenant tenant = getCurrentTenant();

        return classSubjectRepository
                .findAllByTenantId(tenant.getId())
                .stream()
                .filter(cs -> !cs.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClassSubjectResponse> getByClass(
            Long classId
    ) {

        Tenant tenant = getCurrentTenant();

        schoolClassRepository
                .findByIdAndTenantId(
                        classId,
                        tenant.getId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Class not found"
                        )
                );

        return classSubjectRepository
                .findAllBySchoolClassIdAndTenantId(
                        classId,
                        tenant.getId()
                )
                .stream()
                .filter(cs -> !cs.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClassSubjectResponse> getBySubject(
            Long subjectId
    ) {

        Tenant tenant = getCurrentTenant();

        subjectRepository
                .findByIdAndTenantId(
                        subjectId,
                        tenant.getId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Subject not found"
                        )
                );

        return classSubjectRepository
                .findAllBySubjectIdAndTenantId(
                        subjectId,
                        tenant.getId()
                )
                .stream()
                .filter(cs -> !cs.getDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClassSubjectResponse getById(
            Long id
    ) {

        Tenant tenant = getCurrentTenant();

        ClassSubject classSubject =
                classSubjectRepository
                        .findByIdAndTenantId(
                                id,
                                tenant.getId()
                        )
                        .filter(cs -> !cs.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Class-subject assignment not found"
                                )
                        );

        return mapToResponse(classSubject);
    }

    public ClassSubjectResponse update(
            Long id,
            ClassSubjectRequest request
    ) {

        Tenant tenant = getCurrentTenant();

        ClassSubject classSubject =
                classSubjectRepository
                        .findByIdAndTenantId(
                                id,
                                tenant.getId()
                        )
                        .filter(cs -> !cs.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Class-subject assignment not found"
                                )
                        );

        SchoolClass schoolClass =
                schoolClassRepository
                        .findByIdAndTenantId(
                                request.getClassId(),
                                tenant.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Class not found"
                                )
                        );
        Teacher teacher = null;

        if (request.getTeacherId() != null) {
            teacher = teacherRepository
                    .findByIdAndTenantId(
                            request.getTeacherId(),
                            tenant.getId()
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Teacher not found"
                            )
                    );
        }

        Subject subject =
                subjectRepository
                        .findByIdAndTenantId(
                                request.getSubjectId(),
                                tenant.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Subject not found"
                                )
                        );

        boolean duplicate =
                classSubjectRepository
                        .existsBySchoolClassIdAndSubjectIdAndAcademicYearAndTenantId(
                                schoolClass.getId(),
                                subject.getId(),
                                request.getAcademicYear(),
                                tenant.getId()
                        );

        if (duplicate
                && !(
                classSubject.getSchoolClass()
                        .getId()
                        .equals(schoolClass.getId())
                        && classSubject.getSubject()
                        .getId()
                        .equals(subject.getId())
                        && classSubject.getAcademicYear()
                        .equals(request.getAcademicYear())
        )) {

            throw new DuplicateResourceException(
                    "Subject is already assigned to this class for this academic year"
            );
        }

        classSubject.setSchoolClass(
                schoolClass
        );

        classSubject.setSubject(
                subject
        );

        classSubject.setAcademicYear(
                request.getAcademicYear()
        );

        classSubject.setWeeklyPeriods(
                request.getWeeklyPeriods()
        );
        classSubject.setTeacher(teacher);

        return mapToResponse(
                classSubjectRepository.save(
                        classSubject
                )
        );
    }

    public void delete(Long id) {

        Tenant tenant = getCurrentTenant();

        ClassSubject classSubject =
                classSubjectRepository
                        .findByIdAndTenantId(
                                id,
                                tenant.getId()
                        )
                        .filter(cs -> !cs.getDeleted())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Class-subject assignment not found"
                                )
                        );

        classSubject.setDeleted(true);
        classSubject.setActive(false);

        classSubjectRepository.save(
                classSubject
        );
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

    private ClassSubjectResponse mapToResponse(
            ClassSubject classSubject
    ) {

        SchoolClass schoolClass =
                classSubject.getSchoolClass();

        Subject subject =
                classSubject.getSubject();

        return ClassSubjectResponse.builder()
                .id(classSubject.getId())
                .classId(schoolClass.getId())
                .className(schoolClass.getName())
                .section(schoolClass.getSection())
                .subjectId(subject.getId())
                .subjectName(subject.getName())
                .subjectCode(subject.getCode())
                .teacherId(
                        classSubject.getTeacher() != null
                                ? classSubject.getTeacher().getId()
                                : null
                )
                .teacherName(
                        classSubject.getTeacher() != null
                                ? classSubject.getTeacher().getFirstName()
                                + " "
                                + classSubject.getTeacher().getLastName()
                                : null
                )
                .academicYear(
                        classSubject.getAcademicYear()
                )
                .weeklyPeriods(
                        classSubject.getWeeklyPeriods()
                )
                .active(
                        classSubject.getActive()
                )
                .build();
    }
}