package com.braincampus.student.attendance.service;
import com.braincampus.exception.DuplicateResourceException;
import com.braincampus.exception.ResourceNotFoundException;
import com.braincampus.security.SecurityUtils;
import com.braincampus.student.attendance.dto.AttendanceEntryRequest;
import com.braincampus.student.attendance.dto.AttendanceResponse;
import com.braincampus.student.attendance.dto.BulkAttendanceRequest;
import com.braincampus.student.attendance.entity.StudentAttendance;
import com.braincampus.student.attendance.repository.StudentAttendanceRepository;
import com.braincampus.student.entity.Student;
import com.braincampus.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentAttendanceService {

    private final StudentAttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    public List<AttendanceResponse> markBulkAttendance(
            BulkAttendanceRequest request
    ) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        List<StudentAttendance> attendanceRecords =
                request.getAttendance()
                        .stream()
                        .map(entry -> createAttendance(
                                entry,
                                request,
                                tenantId
                        ))
                        .toList();

        return attendanceRepository
                .saveAll(attendanceRecords)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private StudentAttendance createAttendance(
            AttendanceEntryRequest entry,
            BulkAttendanceRequest request,
            Long tenantId
    ) {

        Student student = studentRepository
                .findByIdAndTenantId(
                        entry.getStudentId(),
                        tenantId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found"
                        )
                );

        if (attendanceRepository
                .existsByTenantIdAndStudentIdAndAttendanceDate(
                        tenantId,
                        student.getId(),
                        request.getAttendanceDate()
                )) {

            throw new DuplicateResourceException(
                    "Attendance already marked for student "
                            + student.getId()
                            + " on "
                            + request.getAttendanceDate()
            );
        }

        return StudentAttendance.builder()
                .tenant(student.getTenant())
                .student(student)
                .attendanceDate(request.getAttendanceDate())
                .status(entry.getStatus())
                .remarks(entry.getRemarks())
                .build();
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getStudentAttendance(
            Long studentId
    ) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        // First verify that the student belongs to this school.
        studentRepository
                .findByIdAndTenantId(studentId, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found"
                        )
                );

        return attendanceRepository
                .findAllByTenantIdAndStudentId(
                        tenantId,
                        studentId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getStudentAttendanceBetween(
            Long studentId,
            java.time.LocalDate from,
            java.time.LocalDate to
    ) {

        Long tenantId = SecurityUtils.getCurrentTenantId();

        studentRepository
                .findByIdAndTenantId(studentId, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found"
                        )
                );

        return attendanceRepository
                .findAllByTenantIdAndStudentIdAndAttendanceDateBetween(
                        tenantId,
                        studentId,
                        from,
                        to
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AttendanceResponse mapToResponse(
            StudentAttendance attendance
    ) {

        Student student = attendance.getStudent();

        return AttendanceResponse.builder()
                .id(attendance.getId())
                .studentId(student.getId())
                .roleNumber(student.getRoleNumber())
                .studentName(buildStudentName(student))
                .attendanceDate(attendance.getAttendanceDate())
                .status(attendance.getStatus())
                .remarks(attendance.getRemarks())
                .schoolCode(attendance.getTenant().getSchoolCode())
                .build();
    }

    private String buildStudentName(Student student) {

        if (student.getLastName() == null ||
                student.getLastName().isBlank()) {

            return student.getFirstName();
        }

        return student.getFirstName()
                + " "
                + student.getLastName();
    }
}