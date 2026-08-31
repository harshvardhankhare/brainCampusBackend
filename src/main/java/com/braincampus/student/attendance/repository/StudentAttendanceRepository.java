package com.braincampus.student.attendance.repository;
import com.braincampus.student.attendance.entity.StudentAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentAttendanceRepository
        extends JpaRepository<StudentAttendance, Long> {

    List<StudentAttendance> findAllByTenantIdAndStudentId(
            Long tenantId,
            Long studentId
    );

    List<StudentAttendance> findAllByTenantIdAndStudentIdAndAttendanceDateBetween(
            Long tenantId,
            Long studentId,
            LocalDate from,
            LocalDate to
    );

    Optional<StudentAttendance> findByIdAndTenantId(
            Long id,
            Long tenantId
    );

    Optional<StudentAttendance> findByTenantIdAndStudentIdAndAttendanceDate(
            Long tenantId,
            Long studentId,
            LocalDate attendanceDate
    );

    boolean existsByTenantIdAndStudentIdAndAttendanceDate(
            Long tenantId,
            Long studentId,
            LocalDate attendanceDate
    );
}