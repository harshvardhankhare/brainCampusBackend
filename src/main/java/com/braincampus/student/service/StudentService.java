package com.braincampus.student.service;
import com.braincampus.exception.DuplicateResourceException;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.security.SecurityUtils;
import com.braincampus.student.dto.PageResponse;
import com.braincampus.student.dto.StudentRequest;
import com.braincampus.student.dto.StudentResponse;
import com.braincampus.student.entity.Student;
import com.braincampus.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.braincampus.schoolClass.entity.SchoolClass;
import com.braincampus.schoolClass.repository.SchoolClassRepository;
import org.springframework.data.domain.Pageable;
@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;

    public StudentResponse create(StudentRequest request) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        if (studentRepository.existsByRoleNumberAndTenantId(
                request.getRoleNumber(),
                tenantId
        )) {
            throw new DuplicateResourceException(
                    "Role number already exists"
            );
        }
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

        Student student = new Student();

        student.setRoleNumber(request.getRoleNumber());
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setAddress(request.getAddress());
        student.setParentName(request.getParentName());
        student.setParentPhone(request.getParentPhone());
        student.setActive(true);
        student.setSchoolClass(schoolClass);
        student.setTenant(
                SecurityUtils.getCurrentUser().getUser().getTenant()
        );

        student = studentRepository.save(student);

        return mapToResponse(student);
    }

    @Transactional(readOnly = true)
    public PageResponse<StudentResponse> getAll(
            Long classId,
            String academicYear,
            Pageable pageable
    ) {
        Long tenantId = SecurityUtils.getCurrentTenantId();

        Page<Student> studentPage;

        if (classId != null || academicYear != null) {
            studentPage = studentRepository.findStudentsWithFilter(
                    tenantId,
                    classId,
                    academicYear,
                    pageable
            );
        } else {
            studentPage = studentRepository.findAllByTenantId(tenantId, pageable);
        }

        Page<StudentResponse> responsePage = studentPage.map(this::mapToResponse);

        return PageResponse.from(responsePage);
    }

    private StudentResponse mapToResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .roleNumber(student.getRoleNumber())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .phone(student.getPhone())
                .dateOfBirth(student.getDateOfBirth())
                .address(student.getAddress())
                .parentName(student.getParentName())
                .parentPhone(student.getParentPhone())
                .active(student.getActive())
                .schoolCode(student.getTenant().getSchoolCode())
                .classId(student.getSchoolClass().getId())
                .className(student.getSchoolClass().getName())
                .section(student.getSchoolClass().getSection())
                .academicYear(student.getSchoolClass().getAcademicYear())
                .build();
    }

    @Transactional(readOnly = true)
    public StudentResponse getById(Long id) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        Student student = studentRepository
                .findByIdAndTenantId(id, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found"
                        ));

        return mapToResponse(student);
    }

    public StudentResponse update(
            Long id,
            StudentRequest request
    ) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        Student student = studentRepository
                .findByIdAndTenantId(id, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found"
                        ));

        if (!student.getRoleNumber().equals(request.getRoleNumber())
                && studentRepository.existsByRoleNumberAndTenantId(
                request.getRoleNumber(),
                tenantId
        )) {

            throw new DuplicateResourceException(
                    "Role number already exists"
            );
        }
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

        student.setRoleNumber(request.getRoleNumber());
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setAddress(request.getAddress());
        student.setParentName(request.getParentName());
        student.setParentPhone(request.getParentPhone());
        student.setSchoolClass(schoolClass);
        student = studentRepository.save(student);

        return mapToResponse(student);
    }

    public void delete(Long id) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        Student student = studentRepository
                .findByIdAndTenantId(id, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found"
                        ));

        studentRepository.delete(student);
    }
}
